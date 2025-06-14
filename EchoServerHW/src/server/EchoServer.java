package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final ExecutorService pool = Executors.newCachedThreadPool();
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
            while(!serverSocket.isClosed()){
                Socket clientSocket = serverSocket.accept();
                pool.submit(() -> {
                    try {
                        handle(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
//            try (Socket socket = serverSocket.accept()) {
//                System.out.println("Client connected");
//
//            }
        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped");
        } catch (IOException io) {
            System.out.println("Client disconnected");
        }
    }

    public void handle(Socket socket) throws IOException {
        System.out.printf("Client %s connected%n", socket);

        try(
                socket;
                Scanner reader = getReader(socket);
                PrintWriter writer = getWriter(socket);
        ) {
            sendResponse(writer, "Hello " + socket);
            while (true) {
                String message = reader.nextLine().strip();
                System.out.println("Got message from client: " + message);

                if (isQuitMsg(message) || isEmptyMsg(message)) {
                    break;
                }

            }
        } catch (NoSuchElementException nsee) {
            System.out.println("Client dropped connection");
        } catch(IOException io){
            io.printStackTrace();
        }
        System.out.printf("Client %s disconnected%n", socket);
    }

    public Scanner getReader(Socket socket) throws IOException {
        InputStream input = socket.getInputStream();
        InputStreamReader reader = new InputStreamReader(input);
        return new Scanner(reader);
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream output = socket.getOutputStream();
        return new PrintWriter(output);
    }

    private void sendResponse(Writer writer, String response) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private Boolean isQuitMsg(String msg){
        return msg.equalsIgnoreCase("bye");
    }

    private Boolean isEmptyMsg(String msg){
        return msg == null || msg.isEmpty();
    }
}