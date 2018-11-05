package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.RelativeEntityPositionMessage;

public final class RelativeEntityPositionCodec implements Codec<RelativeEntityPositionMessage> {

    @Override
    public RelativeEntityPositionMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        short deltaX = buf.readShort();
        short deltaY = buf.readShort();
        short deltaZ = buf.readShort();
        boolean onGround = buf.readBoolean();
        return new RelativeEntityPositionMessage(id, deltaX, deltaY, deltaZ, onGround);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, RelativeEntityPositionMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        buf.writeShort(message.getDeltaX());
        buf.writeShort(message.getDeltaY());
        buf.writeShort(message.getDeltaZ());
        buf.writeBoolean(message.isOnGround());
        return buf;
    }
}
