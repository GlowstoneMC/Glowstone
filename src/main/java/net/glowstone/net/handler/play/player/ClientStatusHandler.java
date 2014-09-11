package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ClientStatusMessage;
import org.bukkit.Achievement;

public final class ClientStatusHandler implements MessageHandler<GlowSession, ClientStatusMessage> {
    @Override
    public void handle(GlowSession session, ClientStatusMessage message) {
        final GlowPlayer player = session.getPlayer();

        switch (message.getAction()) {
            case ClientStatusMessage.RESPAWN:
                player.respawn();
                break;

            case ClientStatusMessage.REQUEST_STATS:
                player.sendStats();
                break;

            case ClientStatusMessage.OPEN_INVENTORY:
                player.awardAchievement(Achievement.OPEN_INVENTORY);
                break;

            default:
                GlowServer.logger.info(session + " sent unknown ClientStatus action: " + message.getAction());
        }
    }
}
