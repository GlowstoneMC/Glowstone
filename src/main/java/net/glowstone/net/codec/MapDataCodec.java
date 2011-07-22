package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.MapDataMessage;

public final class MapDataCodec extends MessageCodec<MapDataMessage> {

    public MapDataCodec() {
        super(MapDataMessage.class, 0x83);
    }

    @Override
    public MapDataMessage decode(ChannelBuffer buffer) throws IOException {
        short type = buffer.readShort();
        short id = buffer.readShort();
        short size = buffer.readUnsignedByte();
        
        byte[] data = new byte[size];
        for (int i = 0; i < data.length; ++i) {
            data[i] = buffer.readByte();
        }
        
        return new MapDataMessage(type, id, data);
    }

    @Override
    public ChannelBuffer encode(MapDataMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeShort(message.getType());
        buffer.writeShort(message.getId());
        buffer.writeByte(message.getData().length);
        buffer.writeBytes(message.getData());
        return buffer;
    }

}
