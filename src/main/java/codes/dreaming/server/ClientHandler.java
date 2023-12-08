package codes.dreaming.server;

import codes.dreaming.comms.ConnectionState;
import codes.dreaming.comms.client.*;
import codes.dreaming.comms.server.*;
import codes.dreaming.comms.server.Error;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private ConnectionState state = ConnectionState.CONNECTED;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String sender, String recipient, String message) {
        try {
            output.writeObject(new ServerMessagePacket(message, sender, recipient));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(ServerPacket packet) throws IOException {
        output.writeObject(packet);
    }

    @Override
    public void run() {
        // Setup streams
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error setting up streams");
            return;
        }

        // Authentication loop
        while (state.equals(ConnectionState.CONNECTED)) {
            try {
                ClientPacket packet = (ClientPacket) input.readObject();

                if (packet instanceof ClientAuthPacket authPacket) {
                    this.username = authPacket.getUsername();
                    if (this.server.addUser(this)) {
                        this.state = ConnectionState.AUTHENTICATED;
                        output.writeObject(new ServerConnectionStateUpdatePacket(ConnectionState.AUTHENTICATED));
                    } else {
                        output.writeObject(new ServerErrorPacket(Error.USERNAME_TAKEN));
                    }
                } else {
                    output.writeObject(new ServerErrorPacket(Error.AUTHENTICATION_NEEDED));
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Invalid packet received");
            } catch (IOException e) {
                System.out.println("Client disconnected");
                return;
            }
        }

        // Main loop
        while (!this.socket.isClosed()) {
            try {
                ClientPacket packet = (ClientPacket) input.readObject();

                if (packet instanceof ClientMessagePacket clientMessagePacket) {
                    this.server.sendMessage(this, clientMessagePacket.getRecipient(), clientMessagePacket.getMessage());
                } else if (packet instanceof ClientListRequestPacket) {
                    this.output.writeObject(this.server.getListPacket());
                } else if (packet instanceof ClientJoinGroupPacket clientJoinGroupPacket) {
                    this.server.joinGroup(this, clientJoinGroupPacket.getGroupName());
                } else if (packet instanceof ClientLeaveGroupPacket clientLeaveGroupPacket) {
                    this.server.leaveGroup(this, clientLeaveGroupPacket.getGroupName());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Invalid packet received");
            } catch (IOException e) {
                System.out.println("Client" + this.username + " disconnected with error");
                e.printStackTrace();
                break;
            }
        }

        this.server.removeUser(this.username);
    }

    // Add methods to handle different packet types and send messages

    private void closeConnection() {
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
