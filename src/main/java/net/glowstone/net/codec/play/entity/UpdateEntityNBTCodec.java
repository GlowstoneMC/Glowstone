package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.UpdateEntityNBTMessage;
import net.glowstone.util.nbt.CompoundTag;

import java.io.IOException;

public final class UpdateEntityNBTCodec implements Codec<UpdateEntityNBTMessage> {

    @Override
    public UpdateEntityNBTMessage decode(ByteBuf buffer) throws IOException {
        int entityId = ByteBufUtils.readVarInt(buffer);
        CompoundTag tag = GlowBufUtils.readCompound(buffer);
        return new UpdateEntityNBTMessage(entityId, tag);
    }

    @Override
    public ByteBuf encode(ByteBuf buf, UpdateEntityNBTMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEntityId());
        GlowBufUtils.writeCompound(buf, message.getTag());
        return buf;
    }
}
