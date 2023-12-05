package codes.dreaming;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // I need to create a chat server in java using no online deps where the flow is the following:
        // When a client connect it sends a packet with the state that can be "needsAuth" or "ready"
        // If the state is "needsAuth" the server will wait for a packet with the username, then the server will reply with an error (alreadyInUse) or go to ready
        // If the state is "ready" the server will wait for the following packets
        // 1. List - The server will reply with a list of users and groups that are available (only groups where at least a user is connected) (nickname)
        // 2. SendMessage - The server will send the message to the user or group with the specified nickname
        // 3. Disconnect(id) - The client will disconnect from the server and the server will send a packet to all the users in the group with the id specified
        // A user receive a message only if is in the group or if the message is sent to him directly
        // The group does not need to be created, it will be created when the first user connects to it
        // The server will send a packet to all the users in the group when a user disconnects
        // The server will send a packet to all the users in the group when a user connects
        // The server will send a packet to all the users in the group when a user sends a message
        // Write a communication package and a server package that implements the server

        System.out.println("1. Server");
        System.out.println("2. Client");
        System.out.print("Choose: ");
        int choice = System.in.read();
        if (choice == '1') {
            //codes.dreaming.server.Main.main(args);
        } else if (choice == '2') {
            //codes.dreaming.client.Main.main(args);
        } else {
            System.out.println("Invalid choice");
        }
    }
}
