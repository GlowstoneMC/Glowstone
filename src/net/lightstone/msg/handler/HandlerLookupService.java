package net.lightstone.msg.handler;

import java.util.HashMap;
import java.util.Map;

import net.lightstone.msg.ChatMessage;
import net.lightstone.msg.HandshakeMessage;
import net.lightstone.msg.IdentificationMessage;
import net.lightstone.msg.KickMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.PositionMessage;
import net.lightstone.msg.PositionRotationMessage;
import net.lightstone.msg.RotationMessage;

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
