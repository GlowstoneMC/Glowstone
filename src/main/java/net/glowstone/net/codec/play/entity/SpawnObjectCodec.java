package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;

import java.io.IOException;

public final class SpawnObjectCodec implements Codec<SpawnObjectMessage> {
    @Override
    public SpawnObjectMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode SpawnObjectMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnObjectMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getType());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getPitch());
        buf.writeByte(message.getYaw());
        buf.writeInt(message.getData());
        if (message.getData() != 0) {
            buf.writeShort(message.getVelX());
            buf.writeShort(message.getVelY());
            buf.writeShort(message.getVelZ());
        }
        return buf;
    }
}
