package net.glowstone.net;

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
	public synchronized void pulse() {
        for (Session session : sessions) {
            synchronized (session) {
                session.pulse();
            }
        }
	}

    /**
     * Adds a new session.
     * @param session The session to add.
     */
	public synchronized void add(Session session) {
        sessions.add(session);
	}

    /**
     * Removes a session.
     * @param session The session to remove.
     */
    public synchronized void remove(Session session) {
        synchronized (session) {
            sessions.remove(session);
        }
    }

}
