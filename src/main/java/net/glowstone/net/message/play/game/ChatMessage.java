package net.glowstone.net.message.play.game;

import net.glowstone.net.message.JsonMessage;
import org.json.simple.JSONObject;

public final class ChatMessage extends JsonMessage {

    private final int mode;

    public ChatMessage(JSONObject json, int mode) {
        super(json);
        this.mode = mode;
    }

    public ChatMessage(String text, int mode) {
        this(toTextJson(text), mode);
    }

    public ChatMessage(JSONObject json) {
        this(json, 0);
    }

    public ChatMessage(String text) {
        this(text, 0);
    }

    public int getMode() {
        return mode;
    }
}
