package net.lightstone.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import net.lightstone.msg.PlayNoteMessage;

public final class PlayNoteCodec extends MessageCodec<PlayNoteMessage> {

	public PlayNoteCodec() {
		super(PlayNoteMessage.class, 0x36);
	}

	@Override
	public PlayNoteMessage decode(ChannelBuffer buffer) throws IOException {
		int x = buffer.readInt();
		int y = buffer.readUnsignedShort();
		int z = buffer.readInt();
		int instrument = buffer.readUnsignedByte();
		int pitch = buffer.readUnsignedByte();
		return new PlayNoteMessage(x, y, z, instrument, pitch);
	}

	@Override
	public ChannelBuffer encode(PlayNoteMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(12);
		buffer.writeInt(message.getX());
		buffer.writeShort(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeByte(message.getInstrument());
		buffer.writeByte(message.getPitch());
		return buffer;
	}

}
