package net.glowstone.net.codec;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.JsonMessage;

import java.io.IOException;

public final class JsonCodec implements Codec<JsonMessage> {
    @Override
    public JsonMessage decode(ByteBuf buffer) throws IOException {
        return new JsonMessage(ByteBufUtils.readUTF8(buffer));
    }

    @Override
    public ByteBuf encode(ByteBuf buf, JsonMessage message) throws IOException {
        ByteBufUtils.writeUTF8(buf, message.getJson());
        return buf;
    }
}
