package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.game.BlockActionMessage;

import java.io.IOException;

public final class BlockActionCodec implements Codec<BlockActionMessage> {
    public BlockActionMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode BlockActionMessage");
    }

    public ByteBuf encode(ByteBuf buf, BlockActionMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeShort(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getData1());
        buf.writeByte(message.getData2());
        ByteBufUtils.writeVarInt(buf, message.getBlockType());
        return buf;
    }
}
