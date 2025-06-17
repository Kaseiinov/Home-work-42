package server;

import com.github.javafaker.Faker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final int port;
    private final Map<String, Socket> connectedClients = new HashMap<>();

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
                Faker faker = new Faker(new Locale("ru"));
                String name = faker.name().firstName();
                connectedClients.put(name, clientSocket);
                System.out.println(name);
                pool.submit(() -> {
                    try {
                        ServerFunction serverFunction = new ServerFunction(connectedClients);
                        serverFunction.handle(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        } catch (NoSuchElementException nse) {
            System.out.println("Client dropped");
        } catch (IOException io) {
            System.out.println("Client disconnected");
        }
    }


}