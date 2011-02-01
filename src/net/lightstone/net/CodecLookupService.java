/*
 * Copyright (c) 2010-2011 Graham Edgecombe.
 *
 * This file is part of Lightstone.
 *
 * Lightstone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Lightstone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightstone.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.lightstone.net;

import java.util.HashMap;
import java.util.Map;

import net.lightstone.msg.Message;
import net.lightstone.net.codec.ActivateItemCodec;
import net.lightstone.net.codec.AnimateEntityCodec;
import net.lightstone.net.codec.AttachEntityCodec;
import net.lightstone.net.codec.BlockChangeCodec;
import net.lightstone.net.codec.ChatCodec;
import net.lightstone.net.codec.CloseWindowCodec;
import net.lightstone.net.codec.CollectItemCodec;
import net.lightstone.net.codec.CompressedChunkCodec;
import net.lightstone.net.codec.CreateEntityCodec;
import net.lightstone.net.codec.DestroyEntityCodec;
import net.lightstone.net.codec.EntityActionCodec;
import net.lightstone.net.codec.EntityInteractionCodec;
import net.lightstone.net.codec.EntityMetadataCodec;
import net.lightstone.net.codec.EntityRotationCodec;
import net.lightstone.net.codec.EntityStatusCodec;
import net.lightstone.net.codec.EntityTeleportCodec;
import net.lightstone.net.codec.EntityVelocityCodec;
import net.lightstone.net.codec.ExplosionCodec;
import net.lightstone.net.codec.GroundCodec;
import net.lightstone.net.codec.HandshakeCodec;
import net.lightstone.net.codec.HealthCodec;
import net.lightstone.net.codec.IdentificationCodec;
import net.lightstone.net.codec.KickCodec;
import net.lightstone.net.codec.LoadChunkCodec;
import net.lightstone.net.codec.MessageCodec;
import net.lightstone.net.codec.MultiBlockChangeCodec;
import net.lightstone.net.codec.OpenWindowCodec;
import net.lightstone.net.codec.PingCodec;
import net.lightstone.net.codec.BlockPlacementCodec;
import net.lightstone.net.codec.DiggingCodec;
import net.lightstone.net.codec.PositionCodec;
import net.lightstone.net.codec.PositionRotationCodec;
import net.lightstone.net.codec.ProgressBarCodec;
import net.lightstone.net.codec.RelativeEntityPositionCodec;
import net.lightstone.net.codec.RelativeEntityPositionRotationCodec;
import net.lightstone.net.codec.RespawnCodec;
import net.lightstone.net.codec.RotationCodec;
import net.lightstone.net.codec.SetWindowSlotCodec;
import net.lightstone.net.codec.SetWindowSlotsCodec;
import net.lightstone.net.codec.SpawnItemCodec;
import net.lightstone.net.codec.SpawnMobCodec;
import net.lightstone.net.codec.SpawnPaintingCodec;
import net.lightstone.net.codec.SpawnVehicleCodec;
import net.lightstone.net.codec.SpawnPlayerCodec;
import net.lightstone.net.codec.SpawnPositionCodec;
import net.lightstone.net.codec.EntityEquipmentCodec;
import net.lightstone.net.codec.TimeCodec;
import net.lightstone.net.codec.TransactionCodec;
import net.lightstone.net.codec.PlayNoteCodec;
import net.lightstone.net.codec.UpdateSignCodec;
import net.lightstone.net.codec.WindowClickCodec;

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
			/* 0x05 */ bind(EntityEquipmentCodec.class);
			/* 0x06 */ bind(SpawnPositionCodec.class);
			/* 0x07 */ bind(EntityInteractionCodec.class);
			/* 0x08 */ bind(HealthCodec.class);
			/* 0x09 */ bind(RespawnCodec.class);
			/* 0x0A */ bind(GroundCodec.class);
			/* 0x0B */ bind(PositionCodec.class);
			/* 0x0C */ bind(RotationCodec.class);
			/* 0x0D */ bind(PositionRotationCodec.class);
			/* 0x0E */ bind(DiggingCodec.class);
			/* 0x0F */ bind(BlockPlacementCodec.class);
			/* 0x10 */ bind(ActivateItemCodec.class);
			/* 0x12 */ bind(AnimateEntityCodec.class);
			/* 0x13 */ bind(EntityActionCodec.class);
			/* 0x14 */ bind(SpawnPlayerCodec.class);
			/* 0x15 */ bind(SpawnItemCodec.class);
			/* 0x16 */ bind(CollectItemCodec.class);
			/* 0x17 */ bind(SpawnVehicleCodec.class);
			/* 0x18 */ bind(SpawnMobCodec.class);
			/* 0x19 */ bind(SpawnPaintingCodec.class);
			/* 0x1C */ bind(EntityVelocityCodec.class);
			/* 0x1D */ bind(DestroyEntityCodec.class);
			/* 0x1E */ bind(CreateEntityCodec.class);
			/* 0x1F */ bind(RelativeEntityPositionCodec.class);
			/* 0x20 */ bind(EntityRotationCodec.class);
			/* 0x21 */ bind(RelativeEntityPositionRotationCodec.class);
			/* 0x22 */ bind(EntityTeleportCodec.class);
			/* 0x26 */ bind(EntityStatusCodec.class);
			/* 0x27 */ bind(AttachEntityCodec.class);
			/* 0x28 */ bind(EntityMetadataCodec.class);
			/* 0x32 */ bind(LoadChunkCodec.class);
			/* 0x33 */ bind(CompressedChunkCodec.class);
			/* 0x34 */ bind(MultiBlockChangeCodec.class);
			/* 0x35 */ bind(BlockChangeCodec.class);
			/* 0x36 */ bind(PlayNoteCodec.class);
			/* 0x3C */ bind(ExplosionCodec.class);
			/* 0x64 */ bind(OpenWindowCodec.class);
			/* 0x65 */ bind(CloseWindowCodec.class);
			/* 0x66 */ bind(WindowClickCodec.class);
			/* 0x67 */ bind(SetWindowSlotCodec.class);
			/* 0x68 */ bind(SetWindowSlotsCodec.class);
			/* 0x69 */ bind(ProgressBarCodec.class);
			/* 0x6A */ bind(TransactionCodec.class);
			/* 0x82 */ bind(UpdateSignCodec.class);
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
