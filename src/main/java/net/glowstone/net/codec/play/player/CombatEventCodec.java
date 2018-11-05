package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.CodecContext;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.CombatEventMessage;
import net.glowstone.net.message.play.player.CombatEventMessage.Event;
import net.glowstone.util.TextMessage;

public final class CombatEventCodec implements Codec<CombatEventMessage> {

    @Override
    public CombatEventMessage decode(CodecContext codecContext, ByteBuf buffer) throws IOException {
        int eventId = ByteBufUtils.readVarInt(buffer);
        Event event = Event.getAction(eventId);
        switch (event) {
            case END_COMBAT: {
                int duration = ByteBufUtils.readVarInt(buffer);
                int entityId = buffer.readInt();
                return new CombatEventMessage(event, duration, entityId);
            }
            case ENTITY_DEAD:
                int playerId = ByteBufUtils.readVarInt(buffer);
                int entityId = buffer.readInt();
                TextMessage message = GlowBufUtils.readChat(buffer);
                return new CombatEventMessage(event, playerId, entityId, message);
            default:
                return new CombatEventMessage(event);
        }
    }

    @Override
    public ByteBuf encode(CodecContext codecContext, ByteBuf buf, CombatEventMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEvent().ordinal());
        if (message.getEvent() == Event.END_COMBAT) {
            ByteBufUtils.writeVarInt(buf, message.getDuration());
            buf.writeInt(message.getEntityId());
        } else if (message.getEvent() == Event.ENTITY_DEAD) {
            ByteBufUtils.writeVarInt(buf, message.getPlayerId());
            buf.writeInt(message.getEntityId());
            GlowBufUtils.writeChat(buf, message.getMessage());
        }
        return buf;
    }
}
