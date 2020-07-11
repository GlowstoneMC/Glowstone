package net.glowstone.net.handler.play.game;

import com.flowpowered.network.MessageHandler;
import net.glowstone.block.GlowBlock;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.NBTQueryResponseMessage;
import net.glowstone.net.message.play.game.QueryBlockNBTMessage;

public final class QueryBlockNBTHandler implements MessageHandler<GlowSession, QueryBlockNBTMessage> {

    @Override
    public void handle(GlowSession session, QueryBlockNBTMessage message) {
        GlowBlock block = session
                .getPlayer()
                .getWorld()
                .getBlockAt(message.getX(), message.getY(), message.getZ());
        String nbt = null; //TODO: get nbt from block
        session.send(new NBTQueryResponseMessage(message.getTransactionID(), nbt));
    }
}
