package net.glowstone.net.codec.play.player;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.play.player.CombatPacket;
import net.glowstone.net.message.play.player.CombatPacket.Event;
import net.glowstone.util.TextMessage;

import java.io.IOException;

public final class CombatEventCodec implements Codec<CombatPacket> {

    @Override
    public CombatPacket decode(ByteBuf buffer) throws IOException {
        int eventId = ByteBufUtils.readVarInt(buffer);
        Event event = Event.getAction(eventId);
        switch (event) {
            case END_COMBAT: {
                int duration = ByteBufUtils.readVarInt(buffer);
                int entityID = buffer.readInt();
                return new CombatPacket(event, duration, entityID);
            }
            case ENTITY_DEAD:
                int playerID = ByteBufUtils.readVarInt(buffer);
                int entityID = buffer.readInt();
                TextMessage message = GlowBufUtils.readChat(buffer);
                return new CombatPacket(event, playerID, entityID, message);
            default:
                return new CombatPacket(event);
        }
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CombatPacket message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEvent().ordinal());
        if (message.getEvent() == Event.END_COMBAT) {
            ByteBufUtils.writeVarInt(buf, message.getDuration());
            buf.writeInt(message.getEntityID());
        } else if (message.getEvent() == Event.ENTITY_DEAD) {
            ByteBufUtils.writeVarInt(buf, message.getPlayerID());
            buf.writeInt(message.getEntityID());
            GlowBufUtils.writeChat(buf, message.getMessage());
        }
        return buf;
    }
}
