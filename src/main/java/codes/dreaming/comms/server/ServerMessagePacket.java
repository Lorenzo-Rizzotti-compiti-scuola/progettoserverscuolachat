package codes.dreaming.comms.server;

public class ServerMessagePacket extends ServerPacket {
    private final String message;

    private final String sentBy;

    private final String recipient;

    public ServerMessagePacket(String message, String sentBy, String recipient) {
        this.message = message;
        this.sentBy = sentBy;
        this.recipient = recipient;
    }

    public String getMessage() {
        return this.message;
    }

    public String getSentBy() {
        return this.sentBy;
    }

    public String getRecipient() {
        return this.recipient;
    }
}
