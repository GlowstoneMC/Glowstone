package net.glowstone.net.message.login;

import com.eatthepath.uuid.FastUUID;
import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.login.LoginStartMessage;
import net.glowstone.util.UuidUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class LoginStartCodec implements Codec<LoginStartMessage> {

    @Override
    public LoginStartMessage decode(ByteBuf buffer) throws IOException {
        String input = ByteBufUtils.readUTF8(buffer);
        boolean hasUuid = buffer.readBoolean();
        UUID uuid = null;
        if (hasUuid) {
            ByteBuf uuidBuf = buffer.readBytes(16);
            uuid = new UUID(uuidBuf.readLong(), uuidBuf.readLong());
        }
        return new LoginStartMessage(input, hasUuid, uuid);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, LoginStartMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getUsername());
        buf.writeBoolean(message.isHasUuid());
        if (message.isHasUuid()) {
            UUID uuid = message.getUuid();
            ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
            bb.putLong(uuid.getMostSignificantBits());
            bb.putLong(uuid.getLeastSignificantBits());
            buf.writeBytes(bb);
        }
        return buf;
    }
}
