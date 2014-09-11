package net.glowstone.net.codec.login;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.LoginStartMessage;

import java.io.IOException;

public final class LoginStartCodec implements Codec<LoginStartMessage> {
    @Override
    public LoginStartMessage decode(ByteBuf buffer) throws IOException {
        return new LoginStartMessage(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginStartMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        return buf;
    }
}
