package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;

import java.io.IOException;

public final class SpawnLightningStrikeCodec implements Codec<SpawnLightningStrikeMessage> {
    @Override
    public SpawnLightningStrikeMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int mode = buf.readByte();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        return new SpawnLightningStrikeMessage(id, mode, x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnLightningStrikeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getMode());
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        return buf;
    }
}
