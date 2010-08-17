package com.grahamedgecombe.smpd.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.grahamedgecombe.smpd.msg.KickMessage;
import com.grahamedgecombe.smpd.util.ChannelBufferUtils;

public final class KickMessageCodec extends MessageCodec<KickMessage> {

	public KickMessageCodec() {
		super(KickMessage.class, 0xFF);
	}

	@Override
	public KickMessage decode(ChannelBuffer buffer) {
		return new KickMessage(ChannelBufferUtils.readString(buffer));
	}

	@Override
	public ChannelBuffer encode(KickMessage message) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferUtils.writeString(buffer, message.getReason());
		return buffer;
	}

}
