package net.glowstone.net.message.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.login.LoginSuccessMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public final class LoginSuccessCodec implements Codec<LoginSuccessMessage> {

    @Override
    public LoginSuccessMessage decode(ByteBuf buffer) throws IOException {
        UUID uuid = GlowBufUtils.readUuid(buffer);
        String username = ByteBufUtils.readUTF8(buffer);
        int numProps = ByteBufUtils.readVarInt(buffer);
        if (numProps > 0) {
            throw new DecoderException("Can't decode LoginSuccessMessage with props");
        }

        return new LoginSuccessMessage(uuid, username, new ArrayList<>());
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginSuccessMessage message) throws IOException {
        GlowBufUtils.writeUuid(buf, message.getUuid());
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        ByteBufUtils.writeVarInt(buf, 0);
        return buf;
    }
}
