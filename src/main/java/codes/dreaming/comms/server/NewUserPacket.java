package codes.dreaming.comms.server;

public class NewUserPacket extends ServerPacket {
    private final String username;
    private final String groupName;

    public NewUserPacket(String username, String groupName) {
        this.username = username;
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public String getGroupName() {
        return groupName;
    }
}
