package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.util.UUID;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.SpawnPaintingMessage;
import org.bukkit.util.BlockVector;

public final class SpawnPaintingCodec implements Codec<SpawnPaintingMessage> {

    @Override
    public SpawnPaintingMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        UUID uuid = GlowBufUtils.readUuid(buf);
        String title = ByteBufUtils.readUTF8(buf);
        BlockVector vector = GlowBufUtils.readBlockPosition(buf);
        int facing = buf.readByte();
        return new SpawnPaintingMessage(id, uuid, title, vector.getBlockX(), vector.getBlockY(),
            vector.getBlockZ(), facing);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, SpawnPaintingMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeUuid(buf, message.getUniqueId());
        ByteBufUtils.writeUTF8(buf, message.getTitle());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getFacing());
        return buf;
    }
}
