package net.glowstone.net.message;

import org.json.simple.JSONObject;

public final class KickMessage extends JsonMessage {

    public KickMessage(JSONObject json) {
        super(json);
    }

    public KickMessage(String json) {
        super(json);
    }

    @Override
    public boolean isAsync() {
        return false;
    }
}
