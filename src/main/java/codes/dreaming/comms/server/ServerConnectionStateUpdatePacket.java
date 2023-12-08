package codes.dreaming.comms.server;

import codes.dreaming.comms.ConnectionState;

public class ServerConnectionStateUpdatePacket extends ServerPacket {
    private final ConnectionState newState;

    public ServerConnectionStateUpdatePacket(ConnectionState newState) {
        this.newState = newState;
    }

    public ConnectionState getState() {
        return newState;
    }
}
