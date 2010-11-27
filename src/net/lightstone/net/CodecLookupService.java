package net.lightstone.net;

import java.util.HashMap;
import java.util.Map;

import net.lightstone.msg.Message;
import net.lightstone.net.codec.ChatMessageCodec;
import net.lightstone.net.codec.CompressedChunkMessageCodec;
import net.lightstone.net.codec.EntityInteractionMessageCodec;
import net.lightstone.net.codec.FlyingMessageCodec;
import net.lightstone.net.codec.HandshakeMessageCodec;
import net.lightstone.net.codec.HealthMessageCodec;
import net.lightstone.net.codec.IdentificationMessageCodec;
import net.lightstone.net.codec.KickMessageCodec;
import net.lightstone.net.codec.LoadChunkMessageCodec;
import net.lightstone.net.codec.MessageCodec;
import net.lightstone.net.codec.PingMessageCodec;
import net.lightstone.net.codec.PositionMessageCodec;
import net.lightstone.net.codec.PositionRotationMessageCodec;
import net.lightstone.net.codec.RespawnMessageCodec;
import net.lightstone.net.codec.RotationMessageCodec;
import net.lightstone.net.codec.SyncInventoryMessageCodec;
import net.lightstone.net.codec.TimeMessageCodec;

public final class CodecLookupService {

	private static MessageCodec<?>[] opcodeTable = new MessageCodec<?>[256];
	private static Map<Class<? extends Message>, MessageCodec<?>> classTable = new HashMap<Class<? extends Message>, MessageCodec<?>>();

	static {
		try {
			/* 0x00 */ bind(PingMessageCodec.class);
			/* 0x01 */ bind(IdentificationMessageCodec.class);
			/* 0x02 */ bind(HandshakeMessageCodec.class);
			/* 0x03 */ bind(ChatMessageCodec.class);
			/* 0x04 */ bind(TimeMessageCodec.class);
			/* 0x05 */ bind(SyncInventoryMessageCodec.class);
			/* 0x06 */ bind(SpawnPositionMessageCodec.class);
			/* 0x07 */ bind(EntityInteractionMessageCodec.class);
			/* 0x08 */ bind(HealthMessageCodec.class);
			/* 0x09 */ bind(RespawnMessageCodec.class);
			/* 0x0A */ bind(FlyingMessageCodec.class);
			/* 0x0B */ bind(PositionMessageCodec.class);
			/* 0x0C */ bind(RotationMessageCodec.class);
			/* 0x0D */ bind(PositionRotationMessageCodec.class);
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
