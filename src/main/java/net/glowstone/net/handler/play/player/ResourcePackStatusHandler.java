package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.constants.ResourcePackStatus;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public final class ResourcePackStatusHandler implements
    MessageHandler<GlowSession, ResourcePackStatusMessage> {

    @Override
    public void handle(GlowSession session, ResourcePackStatusMessage message) {
        Player player = session.getPlayer();
        PlayerResourcePackStatusEvent.Status status = ResourcePackStatus
            .getStatus(message.getResult());
        session.getPlayer().setResourcePackStatus(status);
        EventFactory.callEvent(
            new PlayerResourcePackStatusEvent(player, status, player.getResourcePackHash()));
    }
}
