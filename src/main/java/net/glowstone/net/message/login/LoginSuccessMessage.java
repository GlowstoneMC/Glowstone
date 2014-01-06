package net.glowstone.net.message.login;

import net.glowstone.net.message.Message;
import net.glowstone.util.ChannelBufferUtils;
import org.jboss.netty.buffer.ChannelBuffer;

public class LoginSuccessMessage extends Message {

    private final String uuid;
    private final String username;

    public LoginSuccessMessage(String uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void encode(ChannelBuffer buf) {
        ChannelBufferUtils.writeString(buf, uuid);
        ChannelBufferUtils.writeString(buf, username);
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }
}
