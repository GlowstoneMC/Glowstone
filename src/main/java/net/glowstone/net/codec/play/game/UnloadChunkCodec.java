package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.UnloadChunkPacket;

import java.io.IOException;

public class UnloadChunkCodec implements Codec<UnloadChunkPacket> {
    @Override
    public UnloadChunkPacket decode(ByteBuf buffer) throws IOException {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        return new UnloadChunkPacket(chunkX, chunkZ);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UnloadChunkPacket message) throws IOException {
        buf.writeInt(message.getChunkX());
        buf.writeInt(message.getChunkZ());
        return buf;
    }
}
