package codes.dreaming.server;

import codes.dreaming.comms.ConnectionState;
import codes.dreaming.comms.client.ClientListRequestPacket;
import codes.dreaming.comms.client.ClientMessagePacket;
import codes.dreaming.comms.server.*;
import codes.dreaming.comms.client.ClientAuthPacket;
import codes.dreaming.comms.client.ClientPacket;
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

    @Override
    public void run() {
        try {
            // Setup streams
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            // Authentication loop
            while (state.equals(ConnectionState.CONNECTED)) {
                ClientPacket packet = (ClientPacket) input.readObject();

                if (packet instanceof ClientAuthPacket authPacket) {
                    if(this.server.addUser(this)) {
                        this.username = authPacket.getUsername();
                        this.state = ConnectionState.AUTHENTICATED;
                        output.writeObject(new ServerConnectionStateUpdatePacket(ConnectionState.AUTHENTICATED));
                    }else {
                        output.writeObject(new ServerErrorPacket(Error.USERNAME_TAKEN));
                    }
                }else {
                    output.writeObject(new ServerErrorPacket(Error.AUTHENTICATION_NEEDED));
                }
            }

            // Main loop
            while (true) {
                ClientPacket packet = (ClientPacket) input.readObject();

                if (packet instanceof ClientMessagePacket clientMessagePacket) {
                    this.server.sendMessage(this, clientMessagePacket.getRecipient(), clientMessagePacket.getMessage());
                } else if (packet instanceof ClientListRequestPacket) {
                    this.output.writeObject(this.server.getListPacket());
                }

            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close connections and clean up
            server.removeUser(this.username);
            closeConnection();
        }
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
