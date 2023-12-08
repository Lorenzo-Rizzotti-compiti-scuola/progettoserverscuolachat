package codes.dreaming.client;

import codes.dreaming.comms.ConnectionState;
import codes.dreaming.comms.server.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class HandleIncoming implements Runnable {
    private final Socket socket;
    private final ObjectInputStream input;
    private final ChatClient chatClient;

    public HandleIncoming(ObjectInputStream input, Socket socket, ChatClient chatClient) {
        this.socket = socket;
        this.input = input;
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        while (!this.socket.isClosed()) {
            try {
                ServerPacket packet = (ServerPacket) input.readObject();

                // Clear latest line
                System.out.print("\r");

                if (packet instanceof ServerMessagePacket messagePacket) {
                    System.out.println(messagePacket.getRecipient() + " | " + messagePacket.getSentBy() + ": " + messagePacket.getMessage());
                } else if (packet instanceof NewUserPacket newUserPacket) {
                    System.out.println(newUserPacket.getUsername() + " has joined " + newUserPacket.getGroupName());
                } else if (packet instanceof ServerListPacket userListPacket) {
                    System.out.println("Users online: " + String.join(", ", userListPacket.getUsers()));
                    System.out.println("Groups: " + String.join(", ", userListPacket.getGroups()));
                } else if (packet instanceof ServerErrorPacket errorPacket) {
                    System.out.println("Error: " + errorPacket.getError());
                }

                this.chatClient.printCommandLine();
            } catch (IOException e) {
                System.out.println("Disconnected from server");
                System.exit(0);
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Error: Invalid packet received");
            }
        }
    }
}
