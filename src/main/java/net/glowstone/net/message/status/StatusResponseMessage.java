package net.glowstone.net.message.status;

import com.flowpowered.networking.Message;
import lombok.Data;
import org.json.simple.JSONObject;

@Data
public final class StatusResponseMessage implements Message {

    private final String json;

    public StatusResponseMessage(JSONObject json) {
        this.json = json.toJSONString();
    }

}
