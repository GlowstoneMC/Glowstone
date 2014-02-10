package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.DiggingMessage;

import java.io.IOException;

public final class DiggingCodec implements Codec<DiggingMessage> {
    public DiggingMessage decode(ByteBuf buf) throws IOException {
        int state = buf.readByte();
        int x = buf.readInt();
        int y = buf.readUnsignedByte();
        int z = buf.readInt();
        int face = buf.readByte();
        return new DiggingMessage(state, x, y, z, face);
    }

    public ByteBuf encode(ByteBuf buf, DiggingMessage message) throws IOException {
        buf.writeByte(message.getState());
        buf.writeInt(message.getX());
        buf.writeByte(message.getY());
        buf.writeInt(message.getZ());
        buf.writeByte(message.getFace());
        return buf;
    }
}
