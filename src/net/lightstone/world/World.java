package net.lightstone.world;

import java.util.Collection;

import net.lightstone.io.ChunkIoService;
import net.lightstone.model.ChunkManager;
import net.lightstone.model.Entity;
import net.lightstone.model.EntityManager;
import net.lightstone.model.Player;
import net.lightstone.model.Position;
import net.lightstone.msg.ChatMessage;

public class World {

	private final Position spawnPosition = new Position(0, 63, 0);

	private final ChunkManager chunks;

	private final EntityManager entities = new EntityManager();

	public World(ChunkIoService service, WorldGenerator generator) {
		chunks = new ChunkManager(service, generator);
	}

	public void pulse() {
		for (Entity entity : entities)
			entity.pulse();

		for (Entity entity : entities)
			entity.reset();
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

	public Position getSpawnPosition() {
		return spawnPosition;
	}

	public void broadcastMessage(String text) {
		ChatMessage message = new ChatMessage(text);
		for (Player player : getPlayers())
			player.getSession().send(message);
	}

}
