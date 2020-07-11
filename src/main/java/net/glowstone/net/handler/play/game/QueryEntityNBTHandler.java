package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowEntity;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.NBTQueryResponseMessage;
import net.glowstone.net.message.play.game.QueryEntityNBTMessage;

import java.util.UUID;

public class QueryEntityNBTHandler implements MessageHandler<GlowSession, QueryEntityNBTMessage> {
    @Override
    public void handle(GlowSession session, QueryEntityNBTMessage message) {
        GlowEntity entity = session.getPlayer().getWorld().getEntityManager().getEntity(message.getEntityID());
        String nbt = null; //TODO: get nbt from entity
        session.send(new NBTQueryResponseMessage(message.getTransactionID(), nbt));
    }
}
