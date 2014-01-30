package net.glowstone.net.message.play.game;

import net.glowstone.net.message.JsonMessage;
import org.json.simple.JSONObject;

public final class ChatMessage extends JsonMessage {

    public ChatMessage(JSONObject json) {
        super(json);
    }

    public ChatMessage(String text) {
        super(toTextJson(text));
    }
}
