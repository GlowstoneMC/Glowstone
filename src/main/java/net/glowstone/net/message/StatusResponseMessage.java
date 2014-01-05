package net.glowstone.net.message;

import org.json.simple.JSONObject;

public final class StatusResponseMessage extends JsonMessage {
    public StatusResponseMessage(JSONObject json) {
        super(json);
    }
}
