package net.glowstone.net.message;

import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.json.simple.JSONObject;

/**
 * Base class for outgoing Json messages.
 */
public abstract class JsonMessage extends Message {

    private final String json;

    public JsonMessage(JSONObject json) {
        this.json = json.toJSONString();
    }

    // in the future, a constructor from a Json object

    @Override
    public final void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, json);
    }

    public final String getJson() {
        return json;
    }

    @Override
    public String toString() {
        // ClassNameMessage{"json": "values"}
        return getClass().getSimpleName() + json;
    }
}
