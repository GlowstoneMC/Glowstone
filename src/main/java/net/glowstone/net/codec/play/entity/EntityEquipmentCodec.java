package net.glowstone.net.codec.play.entity;

import com.flowpowered.networking.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityEquipmentMessage;

import java.io.IOException;

public final class EntityEquipmentCodec implements Codec<EntityEquipmentMessage> {
    public EntityEquipmentMessage decode(ByteBuf buf) throws IOException {
        throw new DecoderException("Cannot decode EntityEquipmentMessage");
    }

    public ByteBuf encode(ByteBuf buf, EntityEquipmentMessage message) throws IOException {
        buf.writeInt(message.getId());
        buf.writeShort(message.getSlot());
        GlowBufUtils.writeSlot(buf, message.getStack());
        return buf;
    }
}
