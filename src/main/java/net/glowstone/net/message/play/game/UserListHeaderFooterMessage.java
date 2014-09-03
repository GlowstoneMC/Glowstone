package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import org.json.simple.JSONObject;

public final class UserListHeaderFooterMessage implements Message {

    private final JSONObject header, footer;

    public UserListHeaderFooterMessage(JSONObject header, JSONObject footer) {
        this.header = header;
        this.footer = footer;
    }

    public JSONObject getHeader() {
        return header;
    }

    public JSONObject getFooter() {
        return footer;
    }

    @Override
    public String toString() {
        return "PlayerListHeaderFooterCodec{" +
                "header=" + header +
                ", footer=" + footer +
                '}';
    }
}
