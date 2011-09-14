package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.OpenWindowMessage;
import net.glowstone.util.ChannelBufferUtils;

public final class OpenWindowCodec extends MessageCodec<OpenWindowMessage> {

    public OpenWindowCodec() {
        super(OpenWindowMessage.class, 0x64);
    }

    @Override
    public OpenWindowMessage decode(ChannelBuffer buffer) throws IOException {
        int id = buffer.readUnsignedByte();
        int type = buffer.readUnsignedByte();
        String title = ChannelBufferUtils.readString(buffer);
        int slots = buffer.readUnsignedByte();
        return new OpenWindowMessage(id, type, title, slots);
    }

    @Override
    public ChannelBuffer encode(OpenWindowMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeByte(message.getId());
        buffer.writeByte(message.getType());
        ChannelBufferUtils.writeString(buffer, message.getTitle());
        buffer.writeByte(message.getSlots());
        return buffer;
    }

}
