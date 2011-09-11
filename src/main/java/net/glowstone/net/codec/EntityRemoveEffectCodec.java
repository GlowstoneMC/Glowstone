package net.glowstone.net.codec;

import net.glowstone.msg.EntityRemoveEffectMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class EntityRemoveEffectCodec extends MessageCodec<EntityRemoveEffectMessage> {

    public EntityRemoveEffectCodec() {
        super(EntityRemoveEffectMessage.class, 0x2A);
    }

    @Override
    public ChannelBuffer encode(EntityRemoveEffectMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(6);
        buffer.writeInt(message.getId());
        buffer.writeByte(message.getEffect());
        return buffer;
    }

    @Override
    public EntityRemoveEffectMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        byte effect = buffer.readByte();
        return new EntityRemoveEffectMessage(id, effect);
    }
    
}
