package codes.dreaming.comms.client;

public class ClientLeaveGroupPacket extends ClientPacket {
    private final String groupName;

    public ClientLeaveGroupPacket(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
