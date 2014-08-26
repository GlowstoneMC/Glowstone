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
    private final ConcurrentMap<GlowSession, Boolean> sessions = new ConcurrentHashMap<>();

    /**
     * Pulses all the sessions.
     */
    public void pulse() {
        for (GlowSession session : sessions.keySet()) {
            session.pulse();
        }
    }

    /**
     * Adds a new session.
     * @param session The session to add.
     */
    public void add(GlowSession session) {
        sessions.put(session, true);
    }

    /**
     * Removes a session.
     * @param session The session to remove.
     */
    public void remove(GlowSession session) {
        sessions.remove(session);
    }

}
