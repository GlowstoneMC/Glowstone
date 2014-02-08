package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.util.TagCompoundUtils;

import java.io.IOException;

public final class EntityMetadataCodec implements Codec<EntityMetadataMessage> {
    public EntityMetadataMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityMetadataMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityMetadataMessage message) throws IOException {
        buf.writeInt(message.getId());
        TagCompoundUtils.writeParameters(buf, message.getEntries());
        return buf;
    }
}
