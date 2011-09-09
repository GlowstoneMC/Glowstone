package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.StateChangeMessage;

public final class StateChangeCodec extends MessageCodec<StateChangeMessage> {

    public StateChangeCodec() {
        super(StateChangeMessage.class, 0x46);
    }

    @Override
    public StateChangeMessage decode(ChannelBuffer buffer) throws IOException {
        byte state = buffer.readByte();
        byte something = buffer.readByte();
        return new StateChangeMessage(state, something);
    }

    @Override
    public ChannelBuffer encode(StateChangeMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(3);
        buffer.writeByte(message.getState());
        buffer.writeByte(message.getGameMode());
        return buffer;
    }

}
