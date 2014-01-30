package net.glowstone.net.message.status;

import net.glowstone.net.message.JsonMessage;
import org.json.simple.JSONObject;

public final class StatusResponseMessage extends JsonMessage {

    public StatusResponseMessage(JSONObject json) {
        super(json);
    }

}
