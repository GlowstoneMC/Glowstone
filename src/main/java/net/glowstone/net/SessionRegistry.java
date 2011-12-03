package net.glowstone.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A list of all the sessions which provides a convenient {@link #pulse()}
 * method to pulse every session in one operation.
 * @author Graham Edgecombe
 */
public final class SessionRegistry {

    /**
     * A list of the sessions.
     */
    private final ConcurrentMap<Session,Boolean> sessions = new ConcurrentHashMap<Session, Boolean>();

    /**
     * Pulses all the sessions.
     */
    public void pulse() {
        for (Session session : sessions.keySet()) {
            session.pulse();
        }
    }

    /**
     * Adds a new session.
     * @param session The session to add.
     */
    public void add(Session session) {
        sessions.put(session,true);
    }

    /**
     * Removes a session.
     * @param session The session to remove.
     */
    public void remove(Session session) {
        sessions.remove(session);
    }

}
