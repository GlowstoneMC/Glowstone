package net.lightstone.net;

import java.util.HashMap;
import java.util.Map;

import net.lightstone.msg.Message;
import net.lightstone.net.codec.ActivateItemCodec;
import net.lightstone.net.codec.ChatCodec;
import net.lightstone.net.codec.CompressedChunkCodec;
import net.lightstone.net.codec.EntityInteractionCodec;
import net.lightstone.net.codec.FlyingCodec;
import net.lightstone.net.codec.HandshakeCodec;
import net.lightstone.net.codec.HealthCodec;
import net.lightstone.net.codec.IdentificationCodec;
import net.lightstone.net.codec.KickCodec;
import net.lightstone.net.codec.LoadChunkCodec;
import net.lightstone.net.codec.MessageCodec;
import net.lightstone.net.codec.PingCodec;
import net.lightstone.net.codec.BlockPlacementCodec;
import net.lightstone.net.codec.DiggingCodec;
import net.lightstone.net.codec.PositionCodec;
import net.lightstone.net.codec.PositionRotationCodec;
import net.lightstone.net.codec.RespawnCodec;
import net.lightstone.net.codec.RotationCodec;
import net.lightstone.net.codec.SpawnPositionCodec;
import net.lightstone.net.codec.SyncInventoryCodec;
import net.lightstone.net.codec.TimeCodec;

public final class CodecLookupService {

	private static MessageCodec<?>[] opcodeTable = new MessageCodec<?>[256];
	private static Map<Class<? extends Message>, MessageCodec<?>> classTable = new HashMap<Class<? extends Message>, MessageCodec<?>>();

	static {
		try {
			/* 0x00 */ bind(PingCodec.class);
			/* 0x01 */ bind(IdentificationCodec.class);
			/* 0x02 */ bind(HandshakeCodec.class);
			/* 0x03 */ bind(ChatCodec.class);
			/* 0x04 */ bind(TimeCodec.class);
			/* 0x05 */ bind(SyncInventoryCodec.class);
			/* 0x06 */ bind(SpawnPositionCodec.class);
			/* 0x07 */ bind(EntityInteractionCodec.class);
			/* 0x08 */ bind(HealthCodec.class);
			/* 0x09 */ bind(RespawnCodec.class);
			/* 0x0A */ bind(FlyingCodec.class);
			/* 0x0B */ bind(PositionCodec.class);
			/* 0x0C */ bind(RotationCodec.class);
			/* 0x0D */ bind(PositionRotationCodec.class);
			/* 0x0E */ bind(DiggingCodec.class);
			/* 0x0F */ bind(BlockPlacementCodec.class);
			/* 0x10 */ bind(ActivateItemCodec.class);
			/* 0x32 */ bind(LoadChunkCodec.class);
			/* 0x33 */ bind(CompressedChunkCodec.class);
			/* 0xFF */ bind(KickCodec.class);
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
