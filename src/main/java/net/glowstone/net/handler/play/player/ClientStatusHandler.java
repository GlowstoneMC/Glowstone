package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ClientStatusMessage;

public final class ClientStatusHandler implements MessageHandler<GlowSession, ClientStatusMessage> {

    @Override
    public void handle(GlowSession session, ClientStatusMessage message) {
        GlowPlayer player = session.getPlayer();

        switch (message.getAction()) {
            case ClientStatusMessage.RESPAWN:
                player.respawn();
                break;

            case ClientStatusMessage.REQUEST_STATS:
                player.sendStats();
                break;

            default:
                ConsoleMessages.Info.Net.UNKNOWN_CLIENT_STATUS_ACTION.log(
                    session, message.getAction());
        }
    }
}
