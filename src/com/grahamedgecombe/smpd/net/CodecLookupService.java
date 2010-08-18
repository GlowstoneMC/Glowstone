package com.grahamedgecombe.smpd.net;

import java.util.HashMap;
import java.util.Map;

import com.grahamedgecombe.smpd.msg.Message;
import com.grahamedgecombe.smpd.net.codec.ChatMessageCodec;
import com.grahamedgecombe.smpd.net.codec.CompressedChunkMessageCodec;
import com.grahamedgecombe.smpd.net.codec.HandshakeMessageCodec;
import com.grahamedgecombe.smpd.net.codec.IdentificationMessageCodec;
import com.grahamedgecombe.smpd.net.codec.MessageCodec;
import com.grahamedgecombe.smpd.net.codec.KickMessageCodec;
import com.grahamedgecombe.smpd.net.codec.PingMessageCodec;
import com.grahamedgecombe.smpd.net.codec.LoadChunkMessageCodec;
import com.grahamedgecombe.smpd.net.codec.WorldVisibleMessageCodec;

public final class CodecLookupService {

	private static MessageCodec<?>[] opcodeTable = new MessageCodec<?>[256];
	private static Map<Class<? extends Message>, MessageCodec<?>> classTable = new HashMap<Class<? extends Message>, MessageCodec<?>>();

	static {
		try {
			/* 0x00 */ bind(PingMessageCodec.class);
			/* 0x01 */ bind(IdentificationMessageCodec.class);
			/* 0x02 */ bind(HandshakeMessageCodec.class);
			/* 0x03 */ bind(ChatMessageCodec.class);
			/* 0x0A */ bind(WorldVisibleMessageCodec.class);
			/* 0x32 */ bind(LoadChunkMessageCodec.class);
			/* 0x33 */ bind(CompressedChunkMessageCodec.class);
			/* 0xFF */ bind(KickMessageCodec.class);
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static <T extends Message, C extends MessageCodec<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException {
		MessageCodec<T> codec = clazz.newInstance();
		
		opcodeTable[codec.getOpcode()] = codec;
		classTable.put(codec.getType(), codec);
	}

	public static MessageCodec<?> find(int opcode) {
		return opcodeTable[opcode];
	}

	@SuppressWarnings("unchecked")
	public static <T extends Message> MessageCodec<T> find(Class<T> clazz) {
		return (MessageCodec<T>) classTable.get(clazz);
	}

	private CodecLookupService() {
		
	}

}
