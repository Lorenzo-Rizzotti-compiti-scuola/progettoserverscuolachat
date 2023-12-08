package codes.dreaming.server;

import codes.dreaming.comms.server.NewUserPacket;
import codes.dreaming.comms.server.ServerPacket;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Group {
    private final String name;
    private ConcurrentHashMap<String, ClientHandler> users;

    public Group(String name) {
        this.name = name;
        this.users = new ConcurrentHashMap<>();
    }

    public String getName() {
        return name;
    }

    public boolean addUser(ClientHandler clientHandler) {
        if (users.putIfAbsent(clientHandler.getUsername(), clientHandler) == null) {
            this.sendPacket(new NewUserPacket(clientHandler.getUsername(), this.name), clientHandler);
            return true;
        }
        return false;
    }

    public boolean removeUser(String username) {
        // TODO: Send a message to the group that the user has left
        return users.remove(username) != null;
    }

    public Map<String, ClientHandler> getUser() {
        return Collections.unmodifiableMap(users);
    }

    public void sendMessage(String sender, String message, ClientHandler except) {
        for (ClientHandler user : users.values()) {
            if (user.equals(except)) continue;
            user.sendMessage(sender, this.getName(), message);
        }
    }

    public void sendPacket(ServerPacket packet, ClientHandler except) {
        for (ClientHandler user : users.values()) {
            if (user.equals(except)) continue;
            try {
                user.sendPacket(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
