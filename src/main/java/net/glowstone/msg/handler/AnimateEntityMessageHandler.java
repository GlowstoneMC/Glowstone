package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.AnimateEntityMessage;
import net.glowstone.net.Session;

/**
 * A {@link MessageHandler} which handles {@link Entity} animation messages.
 */
public final class AnimateEntityMessageHandler extends MessageHandler<AnimateEntityMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, AnimateEntityMessage message) {
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

