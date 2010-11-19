package net.lightstone.net.codec;

import java.io.IOException;

import net.lightstone.msg.RotationMessage;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public final class RotationMessageCodec extends MessageCodec<RotationMessage> {

	public RotationMessageCodec() {
		super(RotationMessage.class, 0x0C);
	}

	@Override
	public RotationMessage decode(ChannelBuffer buffer) throws IOException {
		float rotation = buffer.readFloat();
		float pitch = buffer.readFloat();
		boolean flying = buffer.readByte() == 1;
		return new RotationMessage(rotation, pitch, flying);
	}

	@Override
	public ChannelBuffer encode(RotationMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(9);
		buffer.writeFloat(message.getRotation());
		buffer.writeFloat(message.getPitch());
		buffer.writeByte(message.isFlying() ? 1 : 0);
		return buffer;
	}

}
