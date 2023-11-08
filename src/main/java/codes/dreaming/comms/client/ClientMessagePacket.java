package codes.dreaming.comms.client;

public class ClientMessagePacket extends ClientPacket{
    private final String message;
    private final String recipient;

    public ClientMessagePacket(String message, String recipient) {
        this.message = message;
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }
}
