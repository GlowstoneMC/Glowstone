package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityMetadataPacket;

import java.io.IOException;
import java.util.List;

public final class EntityMetadataCodec implements Codec<EntityMetadataPacket> {
    @Override
    public EntityMetadataPacket decode(ByteBuf buf) throws IOException {
        int id = ByteBufUtils.readVarInt(buf);
        List<Entry> metadata = GlowBufUtils.readMetadata(buf);
        return new EntityMetadataPacket(id, metadata);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityMetadataPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        GlowBufUtils.writeMetadata(buf, message.getEntries());
        return buf;
    }
}
