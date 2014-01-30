package net.glowstone.net.message;

import org.json.simple.JSONObject;

public final class KickMessage extends JsonMessage {

    public KickMessage(JSONObject json) {
        super(json);
    }

    public KickMessage(String text) {
        super(toTextJson(text));
    }

}
