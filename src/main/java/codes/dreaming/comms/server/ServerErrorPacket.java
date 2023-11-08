package codes.dreaming.comms.server;

public class ServerErrorPacket extends ServerPacket {
    private final Error error;

    public ServerErrorPacket(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }
}
