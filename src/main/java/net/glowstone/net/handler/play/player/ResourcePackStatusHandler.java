package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.constants.ResourcePackStatus;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ResourcePackStatusMessage;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public final class ResourcePackStatusHandler implements MessageHandler<GlowSession, ResourcePackStatusMessage> {
    @Override
    public void handle(GlowSession session, ResourcePackStatusMessage message) {
        EventFactory.callEvent(new PlayerResourcePackStatusEvent(session.getPlayer(), ResourcePackStatus.getStatus(message.getResult()), message.getHash()));
    }
}
