package net.glowstone.net.codec;

import net.glowstone.msg.EntityEffectMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class EntityEffectCodec extends MessageCodec<EntityEffectMessage> {

    public EntityEffectCodec() {
        super(EntityEffectMessage.class, 0x29);
    }

    @Override
    public ChannelBuffer encode(EntityEffectMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(9);
        buffer.writeInt(message.getId());
        buffer.writeByte(message.getEffect());
        buffer.writeByte(message.getAmplifier());
        buffer.writeShort(message.getDuration());
        return buffer;
    }

    @Override
    public EntityEffectMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        byte effect = buffer.readByte();
        byte amplifier = buffer.readByte();
        short duration = buffer.readShort();
        return new EntityEffectMessage(id, effect, amplifier, duration);
    }
    
}
