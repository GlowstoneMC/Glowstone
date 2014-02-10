package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.InteractEntityMessage;

import java.io.IOException;

public final class InteractEntityCodec implements Codec<InteractEntityMessage> {
    public InteractEntityMessage decode(ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int action = buf.readByte();
        return new InteractEntityMessage(id, action);
    }

    public ByteBuf encode(ByteBuf buf, InteractEntityMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getAction());
        return buf;
    }
}
