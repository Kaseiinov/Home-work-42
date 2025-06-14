package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started");
            try (Socket socket = serverSocket.accept()) {
                System.out.println("Client connected");
                InputStream input = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output);


                try(
                        Scanner scanner = new Scanner(reader);
                        writer
                ){
                    while (true) {
                        String message = scanner.nextLine().trim();
                        System.out.println("Got message from client: " + message);

                        String reversedMessage = new StringBuilder(message).reverse().toString();
                        writer.write(reversedMessage);
                        writer.write(System.lineSeparator());
                        writer.flush();

                        if (message.equalsIgnoreCase("bye")) {
                            System.out.println("Bye bye");
                            return;
                        }

                    }
                }


            }
        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped");
        } catch (IOException io) {
            System.out.println("Client disconnected");
        }
    }
}