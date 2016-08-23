package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.StateChangePacket;

import java.io.IOException;

public final class StateChangeCodec implements Codec<StateChangePacket> {
    @Override
    public StateChangePacket decode(ByteBuf buffer) throws IOException {
        int reason = buffer.readByte();
        float value = buffer.readFloat();

        return new StateChangePacket(reason, value);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, StateChangePacket message) throws IOException {
        buf.writeByte(message.getReason());
        buf.writeFloat(message.getValue());
        return buf;
    }
}
