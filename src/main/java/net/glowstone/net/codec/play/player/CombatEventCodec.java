package net.glowstone.net.codec.play.player;

import com.flowpowered.networking.Codec;
import com.flowpowered.networking.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.glowstone.net.message.play.player.CombatEventMessage;

import java.io.IOException;

public final class CombatEventCodec implements Codec<CombatEventMessage> {

    @Override
    public CombatEventMessage decode(ByteBuf buffer) throws IOException {
        int eventId = ByteBufUtils.readVarInt(buffer);
        CombatEventMessage.Event event = CombatEventMessage.Event.getAction(eventId);
        switch (event) {
            case END_COMBAT: {
                int duration = ByteBufUtils.readVarInt(buffer);
                int entityID = buffer.readInt();
                return new CombatEventMessage(event, duration, entityID);
            }
            case ENTITY_DEAD: {
                int playerID = ByteBufUtils.readVarInt(buffer);
                int entityID = buffer.readInt();
                String message = ByteBufUtils.readUTF8(buffer);
                return new CombatEventMessage(event, playerID, entityID, message);
            }
            default:
                return new CombatEventMessage(event);
        }
    }

    @Override
    public ByteBuf encode(ByteBuf buf, CombatEventMessage message) throws IOException {
        ByteBufUtils.writeVarInt(buf, message.getEvent().ordinal());
        if (message.getEvent() == CombatEventMessage.Event.END_COMBAT) {
            ByteBufUtils.writeVarInt(buf, message.getDuration());
            buf.writeInt(message.getEntityID());
        } else if (message.getEvent() == CombatEventMessage.Event.ENTITY_DEAD) {
            ByteBufUtils.writeVarInt(buf, message.getPlayerID());
            buf.writeInt(message.getEntityID());
            ByteBufUtils.writeUTF8(buf, message.getMessage());
        }
        return buf;
    }
}
