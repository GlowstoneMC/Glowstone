package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockBreakAnimationMessage;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public class BlockBreakAnimationCodec implements Codec<BlockBreakAnimationMessage> {
    @Override
    public BlockBreakAnimationMessage decode(ByteBuf buf) throws IOException {
        int entityId = ByteBufUtils.readVarInt(buf);
        BlockVector vector = GlowBufUtils.readBlockPosition(buf);
        int destroyStage = buf.readByte();
        return new BlockBreakAnimationMessage(entityId, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ(), destroyStage);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, BlockBreakAnimationMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getDestroyStage());
        return buf;
    }
}
