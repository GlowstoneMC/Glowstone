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

package net.lightstone.msg.handler;

import net.lightstone.model.Player;
import net.lightstone.msg.ChatMessage;
import net.lightstone.net.Session;

public final class ChatMessageHandler extends MessageHandler<ChatMessage> {

	@Override
	public void handle(Session session, Player player, ChatMessage message) {
		if (player == null)
			return;

		String text = message.getMessage();
		if (text.length() > 100) {
			session.disconnect("Chat message too long.");
		} else if (text.startsWith("/")) {
			// TODO process command
		} else {
			player.getWorld().broadcastMessage("<" + player.getName() + "> " + text);
		}
	}

}
