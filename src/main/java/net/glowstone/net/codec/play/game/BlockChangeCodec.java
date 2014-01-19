package net.glowstone.net.codec.play.game;

import com.flowpowered.networking.util.ByteBufUtils;
import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.game.BlockChangeMessage;

import java.io.IOException;

public final class BlockChangeCodec implements Codec<BlockChangeMessage> {

    @Override
    public BlockChangeMessage decode(ByteBuf buffer) throws IOException {
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        int type = ByteBufUtils.readVarInt(buffer);
        int metadata = buffer.readByte();

        return new BlockChangeMessage(x, y, z, type, metadata);
    }

    @Override
    public void encode(ByteBuf buf, BlockChangeMessage message) throws IOException {
        buf.writeInt(message.getX());
        buf.writeInt(message.getY());
        buf.writeInt(message.getZ());
        ByteBufUtils.writeVarInt(buf, message.getType());
        buf.writeByte(message.getMetadata());


    }
}
