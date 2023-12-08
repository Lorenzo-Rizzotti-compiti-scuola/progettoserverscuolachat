package codes.dreaming.client;

import codes.dreaming.comms.ConnectionState;
import codes.dreaming.comms.client.*;
import codes.dreaming.comms.server.ServerConnectionStateUpdatePacket;
import codes.dreaming.comms.server.ServerPacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private int port;
    private String host;

    private String username;

    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public String doAuth() throws IOException {
        System.out.print("Username: ");
        Scanner scanner = new Scanner(System.in);
        String username = scanner.nextLine();
        output.writeObject(new ClientAuthPacket("@" + username));
        return username;
    }

    public void joinGroup(String groupName) throws IOException {
        output.writeObject(new ClientJoinGroupPacket(groupName));
    }

    public void leaveGroup(String groupName) throws IOException {
        output.writeObject(new ClientLeaveGroupPacket(groupName));
    }

    public void printCommandLine() {
        System.out.print(this.username + "> ");
    }

    public void start() throws IOException, ClassNotFoundException {
        connect();

        // Auth loop
        while (true) {
            this.username = doAuth();

            ServerPacket packet = (ServerPacket) input.readObject();

            if (packet instanceof ServerConnectionStateUpdatePacket serverConnectionStateUpdatePacket) {
                if (serverConnectionStateUpdatePacket.getState().equals(ConnectionState.AUTHENTICATED)) {
                    System.out.println("Successfully authenticated!");
                    break;
                }
            }
        }

        HandleIncoming handleIncoming = new HandleIncoming(input, socket, this);
        new Thread(handleIncoming).start();

        this.joinGroup("#public");
        while (!this.socket.isClosed()) {
            this.printCommandLine();
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            if (message.startsWith("/join ")) {
                String recipient = message.substring(6);
                this.joinGroup(recipient);
                System.out.println("Joined " + recipient);
            } else if (message.startsWith("/leave ")) {
                String recipient = message.substring(7);
                this.leaveGroup(recipient);
                System.out.println("Left " + recipient);
            } else if (message.startsWith("/list")) {
                output.writeObject(new ClientListRequestPacket());
            } else if (message.startsWith("/dm ")) {
                String recipient = message.substring(4, message.indexOf(' ', 4));
                String content = message.substring(message.indexOf(' ', 4) + 1);
                output.writeObject(new ClientMessagePacket(content, recipient));
            } else {
                output.writeObject(new ClientMessagePacket(message, "#public"));
            }
        }
        System.out.println("Disconnected from server");
        System.exit(0);
    }
}
