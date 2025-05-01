import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient {
    private String username;
    private PrintWriter out;
    private BufferedReader in;
    

    public ChatClient() {
        JFrame frame = new JFrame("Chat Client");
        JTextArea messageArea = new JTextArea(20, 50);
        JTextField inputField = new JTextField(40);
        JButton sendButton = new JButton("Send");
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);

        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(sendButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        String serverIP = JOptionPane.showInputDialog("Enter Server IP Address:");
        String portStr = JOptionPane.showInputDialog("Enter Port Number:");
        int port = Integer.parseInt(portStr);
        username = JOptionPane.showInputDialog("Enter your chat name:");

        try {
            Socket socket = new Socket(serverIP, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect to server");
            System.exit(1);
        }

        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            if (!text.isEmpty()) {
                out.println(username + ": " + text);
                inputField.setText("");
            }
        });

        inputField.addActionListener(e -> sendButton.doClick());

        new Thread(() -> {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    messageArea.append(line + "\n");
                }
            } catch (IOException e) {
                messageArea.append("Disconnected from server.\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
        }
    
    }