package net.lightstone.model;

import java.util.HashSet;
import java.util.Set;

import net.lightstone.msg.ChatMessage;
import net.lightstone.msg.GroundMessage;
import net.lightstone.msg.LoadChunkMessage;
import net.lightstone.net.Session;

public final class Player extends Mob {

	private final String name;
	private final Session session;
	private Set<Chunk.Key> knownChunks = new HashSet<Chunk.Key>();
	private Set<Chunk.Key> previousChunks = new HashSet<Chunk.Key>();  

	public Player(Session session, String name) {
		super(session.getServer().getWorld());
		this.name = name;
		this.session = session;

		// stream the initial set of blocks and teleport us
		this.session.send(new ChatMessage("Hello, World!"));
		this.streamBlocks();
		this.session.send(new GroundMessage(true));
	}

	public String getName() {
		return name;
	}

	public void pulse() {
		streamBlocks();
	}

	private void streamBlocks() {
		int centralX = ((int) position.getX()) / Chunk.WIDTH;
		int centralZ = ((int) position.getZ()) / Chunk.HEIGHT;

		for (int x = (centralX - 8); x <= (centralX + 8); x++) {
			for (int z = (centralZ - 8); z <= (centralZ + 8); z++) {
				Chunk.Key key = new Chunk.Key(x, z);
				if (!knownChunks.contains(key)) {
					knownChunks.add(key);
					session.send(new LoadChunkMessage(x, z, true));
					session.send(world.getChunks().getChunk(x, z).toMessage());
				}
				previousChunks.remove(key);
			}
		}

		for (Chunk.Key key : previousChunks) {
			knownChunks.remove(key);
		}

		previousChunks.clear();
		previousChunks.addAll(knownChunks);
	}

	public Session getSession() {
		return session;
	}

}
