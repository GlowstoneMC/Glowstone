package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockActionMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class BlockActionCodec implements Codec<BlockActionMessage> {

    @Override
    public BlockActionMessage decode(ByteBuf buf) throws IOException {
        BlockVector vector = GlowBufUtils.readBlockPosition(buf);
        int data1 = buf.readByte();
        int data2 = buf.readByte();
        int blockType = ByteBufUtils.readVarInt(buf);
        return new BlockActionMessage(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(),
            data1, data2, blockType);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockActionMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getData1());
        buf.writeByte(message.getData2());
        ByteBufUtils.writeVarInt(buf, message.getBlockType());
        return buf;
    }
}
