package server;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ServerFunction {
    private final Map<String, Socket> connectedClients;

    public ServerFunction(Map<String, Socket> connectedClients) {
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
                sendResponseToEveryOne(message, socket);
                System.out.println(getClientNameBySocket(socket) + " -> " + message);

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
        removeClient(socket);
    }

    private void removeClient(Socket socket) {
        String clientName = getClientNameBySocket(socket);
        if (clientName != null) {
            connectedClients.remove(clientName);
        }
    }

    public String getClientNameBySocket(Socket socket){
        for (Map.Entry<String, Socket> entry : connectedClients.entrySet()) {
            if (entry.getValue().equals(socket)) {
                return entry.getKey();
            }
        }
        return "undefined";
    }

    public void sendResponseToEveryOne(String response, Socket sender){
        for (Map.Entry<String, Socket> entry : connectedClients.entrySet()) {
            String senderName = getClientNameBySocket(sender);
            if (entry.getValue() != sender) {
                try {
                    PrintWriter writer = getWriter(entry.getValue());
                    sendResponse(writer, senderName + " -> " + response);

                } catch (IOException io) {
                    io.printStackTrace();
                }
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
