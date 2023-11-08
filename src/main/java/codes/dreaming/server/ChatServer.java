package codes.dreaming.server;

import codes.dreaming.comms.Values;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private int port;
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> users;
    private ConcurrentHashMap<String, Group> groups;

    public ChatServer(int port) {
        this.port = port;
        this.users = new ConcurrentHashMap<>();
        this.groups = new ConcurrentHashMap<>();
    }

    public boolean idExist(String username) {
        if (username.startsWith(Values.GROUP_CHAR)) {
            return groups.containsKey(username);
        } else if(username.startsWith(Values.USER_CHAR)) {
            return users.containsKey(username);
        } else {
            return true;
        }
    }


    /**
     * Adds a new user to the system.
     *
     * @param clientHandler The client handler representing the user to be added.
     * @return True if the user was successfully added, false if the username is invalid or already exists.
     */
    public boolean addUser(ClientHandler clientHandler) {
        // Check if the username is valid
        if (!clientHandler.getUsername().startsWith(Values.USER_CHAR)){
            return false;
        }

        ClientHandler existingUser = users.putIfAbsent(clientHandler.getUsername(), clientHandler);
        // If the user already exists, return false
        return existingUser == null;
    }

    public void removeUser(String username) {
        users.remove(username);
        groups.forEach((name, group) -> group.removeUser(username));
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            new Thread(clientHandler).start();
        }
    }

    // Add methods to manage users, groups, and send packets to them

    public static void main(String[] args) throws IOException {
        int port = 12345; // Choose your port
        ChatServer server = new ChatServer(port);
        server.start();
    }
}
