package net.glowstone.net.message.login;

import net.glowstone.net.message.Message;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public final class LoginStartMessage extends Message {

    private final String username;

    public LoginStartMessage(String username) {
        this.username = username;
    }

    public LoginStartMessage(ChannelBuffer buf) {
        username = ChannelBufferUtils.readString(buf);
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, username);
    }

    public String getUsername() {
        return username;
    }
}
