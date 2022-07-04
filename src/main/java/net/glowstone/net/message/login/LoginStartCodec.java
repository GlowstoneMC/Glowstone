package net.glowstone.net.message.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.LoginStartMessage;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class LoginStartCodec implements Codec<LoginStartMessage> {

    @Override
    public LoginStartMessage decode(ByteBuf buffer) throws IOException {
        String input = ByteBufUtils.readUTF8(buffer);
        boolean hasSigData = buffer.readBoolean();
        long timestamp = 0;
        byte[] publicKey = null;
        byte[] signature = null;
        if (hasSigData) {
            timestamp = buffer.readLong();
            publicKey = new byte[ByteBufUtils.readVarInt(buffer)];
            buffer.readBytes(publicKey);
            signature = new byte[ByteBufUtils.readVarInt(buffer)];
            buffer.readBytes(signature);
        }
        return new LoginStartMessage(URLEncoder.encode(input, StandardCharsets.UTF_8.toString()),
                timestamp, publicKey, signature);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginStartMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        return buf;
    }
}
