package net.glowstone.msg.handler;

import net.glowstone.EventFactory;
import net.glowstone.block.BlockID;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.AnimateEntityMessage;
import net.glowstone.net.Session;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;

/**
 * A {@link MessageHandler} which handles {@link org.bukkit.entity.Entity} animation messages.
 */
public final class AnimateEntityMessageHandler extends MessageHandler<AnimateEntityMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, AnimateEntityMessage message) {
        Block block = player.getTargetBlock(null, 6);
        if (block == null || block.getTypeId() == BlockID.AIR) {
            if (EventFactory.onPlayerInteract(player, Action.LEFT_CLICK_AIR).isCancelled()) return; // TODO: Item interactions
        }
        if (EventFactory.onPlayerAnimate(player).isCancelled()) return;
        switch (message.getAnimation()) {
        case AnimateEntityMessage.ANIMATION_SWING_ARM:
            AnimateEntityMessage toSend = new AnimateEntityMessage(player.getEntityId(), AnimateEntityMessage.ANIMATION_SWING_ARM);
            for (GlowPlayer observer : player.getWorld().getRawPlayers()) {
                if (observer != player && observer.canSee(player)) {
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

