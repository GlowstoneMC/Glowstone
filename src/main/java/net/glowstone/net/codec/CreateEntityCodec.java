package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.CreateEntityMessage;

public final class CreateEntityCodec extends MessageCodec<CreateEntityMessage> {

    public CreateEntityCodec() {
        super(CreateEntityMessage.class, 0x1E);
    }

    @Override
    public CreateEntityMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        return new CreateEntityMessage(id);
    }

    @Override
    public ChannelBuffer encode(CreateEntityMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(4);
        buffer.writeInt(message.getId());
        return buffer;
    }

}
