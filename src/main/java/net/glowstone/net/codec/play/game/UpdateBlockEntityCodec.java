package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateBlockEntityMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.util.BlockVector;

public final class UpdateBlockEntityCodec implements Codec<UpdateBlockEntityMessage> {

    @Override
    public UpdateBlockEntityMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int action = buffer.readByte();
        CompoundTag nbt = GlowBufUtils.readCompound(buffer);
        return new UpdateBlockEntityMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
            action, nbt);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, UpdateBlockEntityMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getAction());
        GlowBufUtils.writeCompound(buf, message.getNbt());
        return buf;
    }
}
