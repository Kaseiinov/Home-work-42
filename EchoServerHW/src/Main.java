import server.EchoServer;

public class Main {
    public static void main(String[] args) {
        EchoServer.bindToPort(8080).run();
    }
}