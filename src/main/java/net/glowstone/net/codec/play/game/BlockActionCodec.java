package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.BlockActionMessage;

import java.io.IOException;

public final class BlockActionCodec implements Codec<BlockActionMessage> {
    @Override
    public BlockActionMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode BlockActionMessage");
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
