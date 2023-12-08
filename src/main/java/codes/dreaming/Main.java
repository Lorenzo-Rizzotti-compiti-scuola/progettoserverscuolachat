package codes.dreaming;

import codes.dreaming.client.ChatClient;
import codes.dreaming.server.ChatServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("1. Server");
        System.out.println("2. Client");
        System.out.print("Choose: ");
        int choice = System.in.read();
        // Clear the buffer
        System.in.read();
        if (choice == '1') {
            ChatServer server = new ChatServer(8912);
            server.start();
        } else if (choice == '2') {
            ChatClient client = new ChatClient("localhost", 8912);
            client.start();
        } else {
            System.out.println("Invalid choice");
        }
    }
}
