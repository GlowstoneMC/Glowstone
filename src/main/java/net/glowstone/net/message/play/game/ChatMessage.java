package net.glowstone.net.message.play.game;

import net.glowstone.net.message.JsonMessage;
import org.json.simple.JSONObject;

public final class ChatMessage extends JsonMessage {

    public ChatMessage(JSONObject json) {
        super(json);
    }

    public ChatMessage(String text) {
        super(makeJson(text));
    }

    @SuppressWarnings("unchecked")
    private static JSONObject makeJson(String text) {
        JSONObject json = new JSONObject();
        json.put("text", text);
        return json;
    }
}
