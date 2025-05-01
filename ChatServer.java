import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * ChatServer listens for incoming client connections and broadcasts messages to all clients.
 */
public class ChatServer {

    // Server socket to listen for client connections
    private ServerSocket serverSocket;

    // A thread-safe list to store all client output streams
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        int port = 12345; // Default port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]); // Use custom port if specified
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number, using default port 12345.");
            }
        }

        ChatServer server = new ChatServer();
        server.startServer(port);
    }

    /**
     * Starts the server on the specified port.
     */
    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Chat server started on port " + port + ".");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                String hostName = clientSocket.getInetAddress().getHostName();

                System.out.println("New client connected: " + hostName + " (" + clientAddress + ")");
                broadcast("SERVER: " + hostName + " (" + clientAddress + ") has joined the chat.");

                // Create a new thread to handle this client
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    /**
     * Sends a message to all connected clients.
     */
    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
        System.out.println("Broadcasted: " + message);
    }

    /**
     * Removes a client from the list.
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getClientName());
    }

    /**
     * Stops the server and releases resources.
     */
    public void stopServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            System.out.println("Chat server stopped.");
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    /**
     * Inner class to handle individual client connections.
     */
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final ChatServer server;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, ChatServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                // Set up input and output streams
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    server.broadcast(inputLine);
                }
            } catch (IOException e) {
                System.err.println("Client connection error: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        public void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
            server.removeClient(this);
        }

        public String getClientName() {
            return socket.getInetAddress().toString();
        }
    }
}
