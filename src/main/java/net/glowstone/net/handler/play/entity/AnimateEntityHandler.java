package net.glowstone.net.handler.play.entity;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.AnimateEntityMessage;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

public final class AnimateEntityHandler implements MessageHandler<GlowSession, AnimateEntityMessage> {
    public void handle(GlowSession session, AnimateEntityMessage message) {
        final GlowPlayer player = session.getPlayer();

        Block block = player.getTargetBlock(null, 6);
        if (block == null || block.getTypeId() == 0) {
            if (EventFactory.onPlayerInteract(player, Action.LEFT_CLICK_AIR).isCancelled())
                return; // TODO: Item interactions
        }

        if (EventFactory.onPlayerAnimate(player).isCancelled())
            return;

        switch (message.getAnimation()) {
            case AnimateEntityMessage.ANIMATION_SWING_ARM:
                AnimateEntityMessage toSend = new AnimateEntityMessage(player.getEntityId(), AnimateEntityMessage.ANIMATION_SWING_ARM);
                for (GlowPlayer observer : player.getWorld().getRawPlayers()) {
                    if (observer != player && observer.canSee((GlowEntity) player)) {
                        observer.getSession().send(toSend);
                    }
                }
                break;
            default:
                // TODO: other things?
                return;
        }
    }
}
