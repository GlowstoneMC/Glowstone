package net.lightstone.world;

import net.lightstone.io.ChunkIoService;
import net.lightstone.model.ChunkManager;
import net.lightstone.model.EntityManager;
import net.lightstone.model.PlayerManager;

public class World {

	private final ChunkManager chunks;

	private final EntityManager entities = new EntityManager();

	private final PlayerManager players = new PlayerManager(); // TODO should this be a part of the entity manager?

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

	public PlayerManager getPlayers() {
		return players;
	}

}
