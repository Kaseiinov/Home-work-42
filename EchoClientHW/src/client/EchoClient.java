package client;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static EchoClient connectToPort(int port){
        String localhost = "localhost";
        return new EchoClient(localhost, port);
    }

    public void run(){
        try(Socket socket = new Socket(host, port)){
            System.out.println("Connected to server");
            InputStream inputStream = socket.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            Scanner fromServer = new Scanner(reader);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            Scanner scanner = new Scanner(System.in, "UTF-8");

            try(scanner; printWriter; fromServer){
                while(true){

                    System.out.print("Enter message:");
                    String message = scanner.nextLine().trim();
                    printWriter.write(message);
                    printWriter.write(System.lineSeparator());
                    printWriter.flush();

                    System.out.println("Got message from server: " + fromServer.nextLine().trim());

                    if(message.equalsIgnoreCase("bye")){
                        System.out.println("Disconnected from server");
                        return;
                    }
                }

            } catch (NoSuchElementException nsee){
                System.out.println("Disconnected from server");
            }

        } catch (IOException io){
            System.out.println("Can't connect to server");
        }
    }
}
