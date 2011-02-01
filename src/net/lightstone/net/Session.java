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

import java.util.ArrayDeque;
import java.util.Queue;

import net.lightstone.Server;
import net.lightstone.model.Player;
import net.lightstone.msg.KickMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.handler.HandlerLookupService;
import net.lightstone.msg.handler.MessageHandler;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

public final class Session {

	public enum State {
		EXCHANGE_HANDSHAKE, EXCHANGE_IDENTIFICATION, GAME;
	}

	private final Server server;
	private final Channel channel;
	private final Queue<Message> messageQueue = new ArrayDeque<Message>();
	private int timeoutCounter = 0;
	private State state = State.EXCHANGE_HANDSHAKE;
	private Player player;

	public Session(Server server, Channel channel) {
		this.server = server;
		this.channel = channel;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		if (this.player != null)
			throw new IllegalStateException();

		this.player = player;
		this.server.getWorld().getPlayers().add(player);
	}

	@SuppressWarnings("unchecked")
	public void pulse() {
		timeoutCounter++;

		Message message;
		while ((message = messageQueue.poll()) != null) {
			MessageHandler<Message> handler = (MessageHandler<Message>) HandlerLookupService.find(message.getClass());
			if (handler != null) {
				handler.handle(this, player, message);
			}
			timeoutCounter = 0;
		}

		if (timeoutCounter >= 6)
			disconnect("Timed out.");
	}

	public void send(Message message) {
		channel.write(message);
	}

	public void disconnect(String reason) {
		channel.write(new KickMessage(reason)).addListener(ChannelFutureListener.CLOSE);
	}

	public Server getServer() {
		return server;
	}

	@Override
	public String toString() {
		return Session.class.getName() + " [address=" + channel.getRemoteAddress() + "]";
	}

	<T extends Message> void messageReceived(T message) {
		messageQueue.add(message);
	}

	void dispose() {
		if (player != null) {
			player.destroy();
			player = null; // in case we are disposed twice
		}
	}

}
