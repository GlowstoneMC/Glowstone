package net.lightstone.net;

import java.util.ArrayList;
import java.util.List;

public final class SessionRegistry {

	private final List<Session> sessions = new ArrayList<Session>();

	public void pulse() {
		for (Session session : sessions)
			session.pulse();
	}

	void add(Session session) {
		sessions.add(session);
	}

	void remove(Session session) {
		sessions.remove(session);
	}

}
