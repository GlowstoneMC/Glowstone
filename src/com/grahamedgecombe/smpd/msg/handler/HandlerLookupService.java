package com.grahamedgecombe.smpd.msg.handler;

import java.util.HashMap;
import java.util.Map;

import com.grahamedgecombe.smpd.msg.Message;
import com.grahamedgecombe.smpd.msg.PingMessage;

public final class HandlerLookupService {

	private static final Map<Class<? extends Message>, MessageHandler<?>> handlers = new HashMap<Class<? extends Message>, MessageHandler<?>>();

	static {
		try {
			bind(PingMessage.class, PingMessageHandler.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static <T extends Message> void bind(Class<T> clazz, Class<? extends MessageHandler<T>> handlerClass) throws InstantiationException, IllegalAccessException {
		MessageHandler<T> handler = handlerClass.newInstance();
		handlers.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Message> MessageHandler<T> find(Class<T> clazz) {
		return (MessageHandler<T>) handlers.get(clazz);
	}

	private HandlerLookupService() {
		
	}

}
