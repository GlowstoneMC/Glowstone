package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPaintingPacket;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class SpawnPaintingCodec implements Codec<SpawnPaintingPacket> {
    @Override
    public SpawnPaintingPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        String title = ByteBufUtils.readUTF8(buf);
        BlockVector vector = GlowBufUtils.readBlockPosition(buf);
        int facing = buf.readByte();
        return new SpawnPaintingPacket(id, title, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), facing);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPaintingPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, message.getTitle());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFacing());
        return buf;
    }
}
