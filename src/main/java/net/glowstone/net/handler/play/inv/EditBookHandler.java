package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.inv.EditBookMessage;

public final class EditBookHandler implements MessageHandler<GlowSession, EditBookMessage> {
    @Override
    public void handle(GlowSession session, EditBookMessage editBookMessage) {
        //TODO: handle packet
    }
}
