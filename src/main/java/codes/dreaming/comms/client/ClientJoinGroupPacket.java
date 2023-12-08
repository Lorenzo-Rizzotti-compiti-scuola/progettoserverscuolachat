package codes.dreaming.comms.client;

public class ClientJoinGroupPacket extends ClientPacket {
    private final String groupName;


    public ClientJoinGroupPacket(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
