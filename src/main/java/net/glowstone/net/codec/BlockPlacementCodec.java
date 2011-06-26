package net.glowstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.glowstone.msg.BlockPlacementMessage;

public final class BlockPlacementCodec extends MessageCodec<BlockPlacementMessage> {

    public BlockPlacementCodec() {
        super(BlockPlacementMessage.class, 0x0F);
    }

    @Override
    public BlockPlacementMessage decode(ChannelBuffer buffer) throws IOException {
        int x = buffer.readInt();
        int y = buffer.readUnsignedByte();
        int z = buffer.readInt();
        int direction = buffer.readUnsignedByte();
        int id = buffer.readUnsignedShort();
        if (id == 0xFFFF) {
            return new BlockPlacementMessage(x, y, z, direction);
        } else {
            int count = buffer.readUnsignedByte();
            int damage = buffer.readUnsignedByte();
            return new BlockPlacementMessage(x, y, z, direction, id, count, damage);
        }
    }

    @Override
    public ChannelBuffer encode(BlockPlacementMessage message) throws IOException {
        int id = message.getId();

        ChannelBuffer buffer = ChannelBuffers.buffer(12);
        buffer.writeInt(message.getX());
        buffer.writeByte(message.getY());
        buffer.writeInt(message.getZ());
        buffer.writeByte(message.getDirection());
        buffer.writeShort(id);
        if (id != -1) {
            buffer.writeByte(message.getCount());
            buffer.writeByte(message.getDamage());
        }
        return buffer;
    }

}
