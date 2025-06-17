package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ServerFunction {
    private final List<Socket> connectedClients;

    public ServerFunction(List<Socket> connectedClients) {
        this.connectedClients = connectedClients;
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
                sendResponseToEveryOne(connectedClients, message, socket);
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

    public void sendResponseToEveryOne(List<Socket> clients, String response, Socket sender){
        for(Socket client : clients){
            if(client == sender) continue;
            try {
                PrintWriter writer = getWriter(client);
                sendResponse(writer, response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
