package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.game.ChunkDataMessage;
import net.glowstone.net.message.play.game.ChunkLightDataMessage;

import java.io.IOException;

public final class ChunkLightDataCodec extends AbstractLightCodec implements Codec<ChunkLightDataMessage> {

    @Override
    public ChunkDataMessage decode(ByteBuf buffer) throws IOException {
        throw new RuntimeException("Cannot decode ChunkDataMessage");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, ChunkLightDataMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getZ());
        encodeLight(buf, message);
        return buf;
    }
}
