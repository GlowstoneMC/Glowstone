package net.glowstone.net.codec.play.game;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChunkLightDataMessage;

public class AbstractLightCodec {

    public void encodeLight(ByteBuf buf, ChunkLightDataMessage message) {
        buf.writeBoolean(message.isTrustEdges());
        GlowBufUtils.writeBitSet(buf, message.getSkyLightMask());
        GlowBufUtils.writeBitSet(buf, message.getBlockLightMask());
        GlowBufUtils.writeBitSet(buf, message.getEmptySkyLightMask());
        GlowBufUtils.writeBitSet(buf, message.getEmptyBlockLightMask());

        ByteBufUtils.writeVarInt(buf, message.getSkyLight().size());
        for (byte[] bytes : message.getSkyLight()) {
            ByteBufUtils.writeVarInt(buf, bytes.length);
            buf.writeBytes(bytes);
        }

        ByteBufUtils.writeVarInt(buf, message.getBlockLight().size());
        for (byte[] bytes : message.getBlockLight()) {
            ByteBufUtils.writeVarInt(buf, bytes.length);
            buf.writeBytes(bytes);
        }
    }

}
