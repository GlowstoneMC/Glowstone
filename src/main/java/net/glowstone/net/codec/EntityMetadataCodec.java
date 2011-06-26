package net.glowstone.net.codec;

import java.io.IOException;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.EntityMetadataMessage;
import net.glowstone.util.ChannelBufferUtils;
import net.glowstone.util.Parameter;

public final class EntityMetadataCodec extends MessageCodec<EntityMetadataMessage> {

    public EntityMetadataCodec() {
        super(EntityMetadataMessage.class, 0x28);
    }

    @Override
    public EntityMetadataMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        List<Parameter<?>> parameters = ChannelBufferUtils.readParameters(buffer);
        return new EntityMetadataMessage(id, parameters);
    }

    @Override
    public ChannelBuffer encode(EntityMetadataMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeInt(message.getId());
        ChannelBufferUtils.writeParameters(buffer, message.getParameters());
        return buffer;
    }

}
