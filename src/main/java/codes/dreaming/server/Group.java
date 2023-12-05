package codes.dreaming.server;

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
        return users.putIfAbsent(clientHandler.getUsername(), clientHandler) == null;
    }

    public boolean removeUser(String username) {
        // TODO: Send a message to the group that the user has left
        return users.remove(username) != null;
    }

    public Map<String, ClientHandler> getUser() {
        return Collections.unmodifiableMap(users);
    }

    public void sendMessage(String sender, String message) {
        for (ClientHandler user : users.values()) {
            user.sendMessage(sender, this.getName(), message);
        }
    }
}
