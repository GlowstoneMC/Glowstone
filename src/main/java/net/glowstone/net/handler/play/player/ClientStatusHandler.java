package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ClientStatusPacket;
import org.bukkit.Achievement;

public final class ClientStatusHandler implements MessageHandler<GlowSession, ClientStatusPacket> {
    @Override
    public void handle(GlowSession session, ClientStatusPacket message) {
        GlowPlayer player = session.getPlayer();

        switch (message.getAction()) {
            case ClientStatusPacket.RESPAWN:
                player.respawn();
                break;

            case ClientStatusPacket.REQUEST_STATS:
                player.sendStats();
                break;

            case ClientStatusPacket.OPEN_INVENTORY:
                player.awardAchievement(Achievement.OPEN_INVENTORY);
                break;

            default:
                GlowServer.logger.info(session + " sent unknown ClientStatus action: " + message.getAction());
        }
    }
}
