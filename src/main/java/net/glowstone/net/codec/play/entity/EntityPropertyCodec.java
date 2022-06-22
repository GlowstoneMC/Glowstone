package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.glowstone.entity.AttributeManager.Property;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;
import org.bukkit.attribute.AttributeModifier;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class EntityPropertyCodec implements Codec<EntityPropertyMessage> {

    @Override
    public EntityPropertyMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode EntityPropertyMessage!");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityPropertyMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        Map<String, Property> props = message.getProperties();
        ByteBufUtils.writeVarInt(buf, props.size());
        for (Entry<String, Property> property : props.entrySet()) {
            ByteBufUtils.writeUTF8(buf, property.getKey());
            buf.writeDouble(property.getValue().getValue());

            Collection<AttributeModifier> modifiers = property.getValue().getModifiers();
            if (modifiers == null) {
                ByteBufUtils.writeVarInt(buf, 0);
            } else {
                ByteBufUtils.writeVarInt(buf, modifiers.size());
                for (AttributeModifier modifier : modifiers) {
                    GlowBufUtils.writeUuid(buf, modifier.getUniqueId());
                    buf.writeDouble(modifier.getAmount());
                    buf.writeByte(modifier.getOperation().ordinal());
                }
            }
        }

        return buf;
    }
}
