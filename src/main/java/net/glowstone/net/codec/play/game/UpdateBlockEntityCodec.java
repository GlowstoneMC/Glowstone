package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.UpdateBlockEntityMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.util.BlockVector;

import java.io.IOException;

public final class UpdateBlockEntityCodec implements Codec<UpdateBlockEntityMessage> {

    @Override
    public UpdateBlockEntityMessage decode(ByteBuf buffer) throws IOException {
        BlockVector pos = GlowBufUtils.readBlockPosition(buffer);
        int action = buffer.readByte();
        CompoundTag nbt = GlowBufUtils.readCompound(buffer);
        return new UpdateBlockEntityMessage(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(),
            action, nbt);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UpdateBlockEntityMessage message) throws IOException {
        GlowBufUtils.writeBlockPosition(buf, message.getX(), message.getY(), message.getZ());
        buf.writeByte(message.getAction());
        GlowBufUtils.writeCompound(buf, message.getNbt());
        return buf;
    }
}
