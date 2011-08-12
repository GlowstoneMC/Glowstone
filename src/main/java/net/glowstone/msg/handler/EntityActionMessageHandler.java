package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.EntityActionMessage;
import net.glowstone.net.Session;

/**
 * A {@link MessageHandler} which handles {@link Entity} action messages.
 */
public final class EntityActionMessageHandler extends MessageHandler<EntityActionMessage> {

    @Override
    public void handle(Session session, GlowPlayer player, EntityActionMessage message) {
        switch (message.getAction()) {
        case EntityActionMessage.ACTION_SNEAKING:
            player.setSneaking(true);
            break;
        case EntityActionMessage.ACTION_STOP_SNEAKING:
            player.setSneaking(false);
            break;
        default:
            // TODO: bed support
            return;
        }
    }

}

