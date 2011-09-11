package net.glowstone.net.codec;

import net.glowstone.msg.ExperienceMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class ExperienceCodec extends MessageCodec<ExperienceMessage> {

    public ExperienceCodec() {
        super(ExperienceMessage.class, 0x2B);
    }

    @Override
    public ChannelBuffer encode(ExperienceMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(5);
        buffer.writeByte(message.getBarValue());
        buffer.writeByte(message.getLevel());
        buffer.writeShort(message.getTotalExp());
        return buffer;
    }

    @Override
    public ExperienceMessage decode(ChannelBuffer buffer) throws IOException {
        byte barValue = buffer.readByte();
        byte level = buffer.readByte();
        short totalExp = buffer.readShort();
        return new ExperienceMessage(barValue, level, totalExp);
    }
    
}
