package net.glowstone.net.codec.play.entity;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.glowstone.entity.AttributeManager.Modifier;
import net.glowstone.entity.AttributeManager.Property;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;

public class EntityPropertyCodec implements Codec<EntityPropertyMessage> {

    @Override
    public EntityPropertyMessage decode(ByteBuf buffer) throws IOException {
        throw new DecoderException("Cannot decode EntityPropertyMessage!");
    }

    @Override
    public ByteBuf encode(ByteBuf buf, EntityPropertyMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getId());
        Map<String, Property> props = message.getProperties();
        buf.writeInt(props.size());
        for (Entry<String, Property> property : props.entrySet()) {
            ByteBufUtils.writeUTF8(buf, property.getKey());
            buf.writeDouble(property.getValue().getValue());

            List<Modifier> modifiers = property.getValue().getModifiers();
            if (modifiers == null) {
                ByteBufUtils.writeVarInt(buf, 0);
            } else {
                ByteBufUtils.writeVarInt(buf, modifiers.size());
                for (Modifier modifier : modifiers) {
                    GlowBufUtils.writeUuid(buf, modifier.getUuid());
                    buf.writeDouble(modifier.getAmount());
                    buf.writeByte(modifier.getOperation());
                }
            }
        }

        return buf;
    }
}
