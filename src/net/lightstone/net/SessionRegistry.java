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

import java.util.ArrayList;
import java.util.List;

/**
 * A list of all the sessions which provides a convenient {@link #pulse()}
 * method to pulse every session in one operation.
 * @author Graham Edgecombe
 */
public final class SessionRegistry {

    /**
     * A list of the sessions.
     */
	private final List<Session> sessions = new ArrayList<Session>();

    /**
     * Pulses all the sessions.
     */
	public void pulse() {
		for (Session session : sessions)
			session.pulse();
	}

    /**
     * Adds a new session.
     * @param session The session to add.
     */
	void add(Session session) {
		sessions.add(session);
	}

    /**
     * Removes a session.
     * @param session The session to remove.
     */
	void remove(Session session) {
		sessions.remove(session);
	}

}
