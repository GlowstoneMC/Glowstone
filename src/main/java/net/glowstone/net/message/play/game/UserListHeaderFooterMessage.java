package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import net.glowstone.util.TextMessage;

public final class UserListHeaderFooterMessage implements Message {

    private final TextMessage header, footer;

    public UserListHeaderFooterMessage(TextMessage header, TextMessage footer) {
        this.header = header;
        this.footer = footer;
    }

    public TextMessage getHeader() {
        return header;
    }

    public TextMessage getFooter() {
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
