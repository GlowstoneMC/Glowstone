package net.lightstone.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PlayerManager implements Iterable<Player> {

	private final List<Player> players = new ArrayList<Player>();

	public void add(Player player) {
		players.add(player);
	}

	public void remove(Player player) {
		players.remove(player);
	}

	@Override
	public Iterator<Player> iterator() {
		return players.iterator();
	}

}
