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

import java.util.logging.Level;
import java.util.logging.Logger;

import net.lightstone.Server;
import net.lightstone.msg.Message;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * A {@link SimpleChannelUpstreamHandler} which processes incoming network
 * events.
 * @author Graham Edgecombe.
 */
public class MinecraftHandler extends SimpleChannelUpstreamHandler {

    /**
     * The logger for this class.
     */
	private static final Logger logger = Logger.getLogger(MinecraftHandler.class.getName());

    /**
     * The server.
     */
	private final Server server;

    /**
     * Creates a new network event handler.
     * @param server The server.
     */
	public MinecraftHandler(Server server) {
		this.server = server;
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		server.getChannelGroup().add(c);

		Session session = new Session(server, c);
		server.getSessionRegistry().add(session);
		ctx.setAttachment(session);

		logger.info("Channel connected: " + c + ".");
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		Channel c = e.getChannel();
		server.getChannelGroup().remove(c);

		Session session = (Session) ctx.getAttachment();
		server.getSessionRegistry().remove(session);
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
