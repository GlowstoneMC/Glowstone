package net.glowstone.net;

import java.util.List;
import java.util.ArrayList;

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
        for (Session session : new ArrayList<Session>(sessions)) {
            session.pulse();
        }
    }

    /**
     * Adds a new session.
     * @param session The session to add.
     */
    public void add(Session session) {
        sessions.add(session);
    }

    /**
     * Removes a session.
     * @param session The session to remove.
     */
    public void remove(Session session) {
        sessions.remove(session);
    }

}
