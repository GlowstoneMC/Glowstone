package net.glowstone.net.codec;

import java.io.IOException;

import net.glowstone.msg.MultiBlockChangeMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class MultiBlockChangeCodec extends MessageCodec<MultiBlockChangeMessage> {

    public MultiBlockChangeCodec() {
        super(MultiBlockChangeMessage.class, 0x34);
    }

    @Override
    public MultiBlockChangeMessage decode(ChannelBuffer buffer) throws IOException {
        int chunkX = buffer.readInt();
        int chunkZ = buffer.readInt();
        int changes = buffer.readUnsignedShort();

        short[] coordinates = new short[changes];
        byte[] types = new byte[changes];
        byte[] metadata = new byte[changes];

        for (int i = 0; i < changes; i++) {
            coordinates[i] = buffer.readShort();
        }
        buffer.readBytes(types);
        buffer.readBytes(metadata);

        return new MultiBlockChangeMessage(chunkX, chunkZ, coordinates, types, metadata);
    }

    @Override
    public ChannelBuffer encode(MultiBlockChangeMessage message) throws IOException {
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeInt(message.getChunkX());
        buffer.writeInt(message.getChunkZ());
        buffer.writeShort(message.getChanges());

        short[] coordinates = message.getCoordinates();
        for (int i = 0; i < coordinates.length; i++) {
            buffer.writeShort(coordinates[i]);
        }

        buffer.writeBytes(message.getTypes());
        buffer.writeBytes(message.getMetadata());
        return buffer;
    }

}
