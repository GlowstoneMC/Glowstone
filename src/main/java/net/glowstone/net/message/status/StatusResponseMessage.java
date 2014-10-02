package net.glowstone.net.message.status;

import com.flowpowered.networking.Message;
import org.json.simple.JSONObject;

public final class StatusResponseMessage implements Message {

    private final String json;

    public StatusResponseMessage(JSONObject json) {
        this.json = json.toJSONString();
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toString() {
        return "StatusResponseMessage" + json;
    }
}
