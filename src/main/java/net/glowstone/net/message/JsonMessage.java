package net.glowstone.net.message;

import com.flowpowered.networking.Message;
import org.json.simple.JSONObject;

public class JsonMessage implements Message {

    private final String json;

    public JsonMessage(JSONObject json) {
        this.json = json.toJSONString();
    }

    public JsonMessage(String json) {
        this.json = json;
    }

    public final String getJson() {
        return json;
    }

    @Override
    public String toString() {
        // ClassNameMessage{"json": "values"}
        return getClass().getSimpleName() + json;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject toTextJson(String text) {
        JSONObject json = new JSONObject();
        json.put("text", text);
        return json;
    }
}
