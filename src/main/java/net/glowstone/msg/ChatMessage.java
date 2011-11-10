package net.glowstone.msg;

public final class ChatMessage extends Message {

    private final String message;

    public ChatMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatMessage{message=" + message + "}";
    }
}
