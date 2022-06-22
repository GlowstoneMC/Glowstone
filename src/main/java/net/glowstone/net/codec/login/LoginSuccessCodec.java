package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.login.LoginSuccessMessage;

import java.io.IOException;
import java.util.UUID;

public final class LoginSuccessCodec implements Codec<LoginSuccessMessage> {

    @Override
    public LoginSuccessMessage decode(ByteBuf buffer) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(buffer);
        String username = ByteBufUtils.readUTF8(buffer);

        return new LoginSuccessMessage(uuid, username);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginSuccessMessage message) throws IOException {
        GlowBufUtils.writeUuid(buf, message.getUuid());
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        ByteBufUtils.writeVarInt(buf, 0);
        return buf;
    }
}
