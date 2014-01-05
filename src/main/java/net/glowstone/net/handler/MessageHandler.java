package net.glowstone.net.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.Message;
import net.glowstone.net.Session;

public abstract class MessageHandler<T extends Message> {

    /**
     * Handle a message for the given session and player.
     * @param session The session being handled.
     * @param player The player being handled.
     * @param message The message to handle.
     */
    public abstract void handle(Session session, GlowPlayer player, T message);

}
