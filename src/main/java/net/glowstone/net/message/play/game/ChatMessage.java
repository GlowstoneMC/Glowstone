package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import net.glowstone.util.TextMessage;
import org.json.simple.JSONObject;

public final class ChatMessage implements Message {

    private final TextMessage text;
    private final int mode;

    public ChatMessage(TextMessage text, int mode) {
        this.text = text;
        this.mode = mode;
    }

    public ChatMessage(JSONObject json) {
        this(new TextMessage(json), 0);
    }

    public ChatMessage(String text) {
        this(new TextMessage(text), 0);
    }

    public TextMessage getText() {
        return text;
    }

    public int getMode() {
        return mode;
    }
}
