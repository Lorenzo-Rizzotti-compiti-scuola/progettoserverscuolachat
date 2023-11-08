package codes.dreaming.comms.client;

import codes.dreaming.comms.BasePacket;

public class ClientAuthPacket extends ClientPacket {
    private final String username;

    public ClientAuthPacket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
