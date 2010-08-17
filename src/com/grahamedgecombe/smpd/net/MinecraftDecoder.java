package com.grahamedgecombe.smpd.net;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.replay.ReplayingDecoder;
import org.jboss.netty.handler.codec.replay.VoidEnum;

import com.grahamedgecombe.smpd.net.codec.MessageCodec;

public class MinecraftDecoder extends ReplayingDecoder<VoidEnum> {

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel c, ChannelBuffer buf, VoidEnum state) throws Exception {
		int opcode = buf.readUnsignedByte();
		
		MessageCodec<?> codec = CodecLookupService.find(opcode);
		if (codec == null) {
			throw new IOException("Unknown operation code: " + opcode + ".");
		}
		
		return codec.decode(buf);
	}

}
