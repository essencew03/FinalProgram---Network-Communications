# FinalProgram---Network-Communications
## Overview

This project is a basic Java client-server chat application that facilitates real-time text messaging communication over a network using TCP sockets.
This project has the concepts of network programming, including socket communication, multithreading, and client-server architecture.

## Features

- **ChatServer.java**: Initializes a server socket to accept incoming client connections. For each connected client, it spawns a new thread to handle communication.
- **ChatClient.java**: Allows users to see the visual window of chats using GUI, connects to the chat server and allows the user to send messages. It also starts a listener thread to receive messages from the server.
- **MessageListenerThread.java**: A dedicated thread for the client that listens for incoming messages from the server and displays them to the user.

## Getting Started

### Compilation

Compile all Java files using the following command:

javac *.java

## Running

Start the server
java ChatServer
java ChatClient

## Following Prompts

Enter:

Server IP Address
Port Number
Username
