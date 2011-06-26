package net.glowstone.msg.handler;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.Message;
import net.glowstone.net.Session;

public abstract class MessageHandler<T extends Message> {

    public abstract void handle(Session session, GlowPlayer player, T message);

}
