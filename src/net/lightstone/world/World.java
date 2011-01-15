package net.lightstone.world;

import java.util.Collection;

import net.lightstone.io.ChunkIoService;
import net.lightstone.model.ChunkManager;
import net.lightstone.model.EntityManager;
import net.lightstone.model.Player;

public class World {

	private final ChunkManager chunks;

	private final EntityManager entities = new EntityManager();

	public World(ChunkIoService service, WorldGenerator generator) {
		chunks = new ChunkManager(service, generator);
	}

	public void pulse() {

	}

	public ChunkManager getChunks() {
		return chunks;
	}

	public EntityManager getEntities() {
		return entities;
	}

	public Collection<Player> getPlayers() {
		return entities.getAll(Player.class);
	}

}
