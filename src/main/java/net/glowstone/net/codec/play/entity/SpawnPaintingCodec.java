package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class SpawnPaintingCodec implements Codec<SpawnPaintingMessage> {
    @Override
    public SpawnPaintingMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        String title = ByteBufUtils.readUTF8(buf);
        BlockVector vector = GlowBufUtils.readBlockPosition(buf);
        int facing = buf.readByte();
        return new SpawnPaintingMessage(id, title, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), facing);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, SpawnPaintingMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        ByteBufUtils.writeUTF8(buf, message.getTitle());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFacing());
        return buf;
    }
}
