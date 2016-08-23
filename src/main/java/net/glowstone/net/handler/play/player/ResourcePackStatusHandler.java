package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.constants.ResourcePackStatus;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.ResourcePackStatusPacket;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public final class ResourcePackStatusHandler implements MessageHandler<GlowSession, ResourcePackStatusPacket> {
    @Override
    public void handle(GlowSession session, ResourcePackStatusPacket message) {
        EventFactory.callEvent(new PlayerResourcePackStatusEvent(session.getPlayer(), ResourcePackStatus.getStatus(message.getResult())));
    }
}
