package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;

import java.io.IOException;
import java.util.List;

public final class EntityMetadataCodec implements Codec<EntityMetadataMessage> {

    @Override
    public EntityMetadataMessage decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        List<Entry> metadata = GlowBufUtils.readMetadata(buf);
        return new EntityMetadataMessage(id, metadata);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityMetadataMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeMetadata(buf, message.getEntries());
        return buf;
    }
}
