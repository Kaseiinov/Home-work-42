package server;

import com.github.javafaker.Faker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EchoServer {
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final int port;
    private final List<Socket> connectedClients = new ArrayList<>();

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
                Faker faker = new Faker();
                connectedClients.add(clientSocket);
                System.out.println("Generated name: " + faker.name().fullName());
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