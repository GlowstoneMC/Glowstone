package net.glowstone.msg.handler;

import java.util.HashMap;
import java.util.Map;

import net.glowstone.msg.ChatMessage;
import net.glowstone.msg.HandshakeMessage;
import net.glowstone.msg.IdentificationMessage;
import net.glowstone.msg.KickMessage;
import net.glowstone.msg.Message;
import net.glowstone.msg.PositionMessage;
import net.glowstone.msg.PositionRotationMessage;
import net.glowstone.msg.RotationMessage;

public final class HandlerLookupService {

	private static final Map<Class<? extends Message>, MessageHandler<?>> handlers = new HashMap<Class<? extends Message>, MessageHandler<?>>();

	static {
		try {
			bind(IdentificationMessage.class, IdentificationMessageHandler.class);
			bind(HandshakeMessage.class, HandshakeMessageHandler.class);
			bind(ChatMessage.class, ChatMessageHandler.class);
			bind(PositionMessage.class, PositionMessageHandler.class);
			bind(RotationMessage.class, RotationMessageHandler.class);
			bind(PositionRotationMessage.class, PositionRotationMessageHandler.class);
			bind(KickMessage.class, KickMessageHandler.class);
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
