package net.glowstone.net.codec;

import net.glowstone.msg.ExperienceOrbMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public class ExperienceOrbCodec extends MessageCodec<ExperienceOrbMessage> {

    public ExperienceOrbCodec() {
        super(ExperienceOrbMessage.class, 0x1A);
    }
    @Override
    public ChannelBuffer encode(ExperienceOrbMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.buffer(19);
        buffer.writeInt(message.getId());
        buffer.writeInt(message.getX());
        buffer.writeInt(message.getY());
        buffer.writeInt(message.getZ());
        buffer.writeShort(message.getCount());
        return buffer;
    }

    @Override
    public ExperienceOrbMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readInt();
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        short count = buffer.readShort();
        return new ExperienceOrbMessage(id, x, y, z, count);
    }
}
