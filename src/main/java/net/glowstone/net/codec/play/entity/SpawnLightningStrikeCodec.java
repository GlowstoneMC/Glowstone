package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.SpawnLightningStrikeMessage;

public final class SpawnLightningStrikeCodec implements Codec<SpawnLightningStrikeMessage> {

    @Override
    public SpawnLightningStrikeMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        int mode = buf.readByte();
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new SpawnLightningStrikeMessage(id, mode, x, y, z);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnLightningStrikeMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeByte(message.getMode());
        buf.writeDouble(message.getX());
        buf.writeDouble(message.getY());
        buf.writeDouble(message.getZ());
        return buf;
    }
}
