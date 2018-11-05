package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.message.play.entity.EntityStatusMessage;

public final class EntityStatusCodec implements Codec<EntityStatusMessage> {

    @Override
    public EntityStatusMessage decode(CodecContext codecContext, ByteBuf buf) throws IOException {
        int id = buf.readInt();
        int status = buf.readByte();
        return new EntityStatusMessage(id, status);
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, EntityStatusMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeByte(message.getStatus());
        return buf;
    }
}
