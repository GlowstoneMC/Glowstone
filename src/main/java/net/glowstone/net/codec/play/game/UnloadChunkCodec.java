package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.game.UnloadChunkMessage;

public class UnloadChunkCodec implements Codec<UnloadChunkMessage> {

    @Override
    public UnloadChunkMessage decode(ByteBuf buffer) throws IOException {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        return new UnloadChunkMessage(chunkX, chunkZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UnloadChunkMessage message) throws IOException {
        buf.writeInt(message.getChunkX());
        buf.writeInt(message.getChunkZ());
        return buf;
    }
}
