package codes.dreaming.comms.server;

public class ServerListPacket extends ServerPacket {
    private final String[] users;
    public final String[] groups;

    public ServerListPacket(String[] users, String[] groups) {
        this.users = users;
        this.groups = groups;
    }

    public String[] getUsers() {
        return this.users;
    }

    public String[] getGroups() {
        return this.groups;
    }
}
