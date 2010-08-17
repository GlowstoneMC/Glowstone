package com.grahamedgecombe.smpd.msg.handler;

import com.grahamedgecombe.smpd.msg.Message;
import com.grahamedgecombe.smpd.net.Session;

public abstract class MessageHandler<T extends Message> {

	public abstract void handle(Session session, T message);

}
