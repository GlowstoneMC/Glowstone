package com.grahamedgecombe.smpd.net;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;

import com.grahamedgecombe.smpd.msg.Message;

public class MinecraftHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(MinecraftHandler.class.getName());

	private final ChannelGroup group;

	public MinecraftHandler(ChannelGroup group) {
		this.group = group;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		group.add(c);
		
		ctx.setAttachment(new Session(c));
		
		logger.info("Channel connected: " + c + ".");
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		group.remove(c);
		
		Session session = (Session) ctx.getAttachment();
		session.dispose();
		
		logger.info("Channel disconnected: " + c + ".");
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		Session session = (Session) ctx.getAttachment();
		session.messageReceived((Message) e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		Channel c = e.getChannel();
		if (c.isOpen()) {
			logger.log(Level.WARNING, "Exception caught, closing channel: " + c + "...", e.getCause());
			c.close();
		}
	}

}
