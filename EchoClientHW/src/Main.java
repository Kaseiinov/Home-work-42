import client.EchoClient;

public class Main {
    public static void main(String[] args) {
        EchoClient.connectToPort(8080).run();
    }
}