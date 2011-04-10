package net.glowstone.world;

import java.util.Collection;

import org.bukkit.Location;

import net.glowstone.io.ChunkIoService;
import net.glowstone.model.ChunkManager;
import net.glowstone.model.Entity;
import net.glowstone.model.EntityManager;
import net.glowstone.model.Player;
import net.glowstone.msg.ChatMessage;

/**
 * A class which represents the in-game world.
 * @author Graham Edgecombe
 */
public class World {

	/**
	 * The spawn position.
	 */
	private final Location spawnLocation = new Location(null, 0, 63, 0);

	/**
	 * The chunk manager.
	 */
	private final ChunkManager chunks;

	/**
	 * The entity manager.
	 */
	private final EntityManager entities = new EntityManager();

	/**
	 * Creates a new world with the specified chunk I/O service and world
	 * generator.
	 * @param service The chunk I/O service.
	 * @param generator The world generator.
	 */
	public World(ChunkIoService service, WorldGenerator generator) {
		chunks = new ChunkManager(service, generator);
	}

	/**
	 * Updates all the entities within this world.
	 */
	public void pulse() {
		for (Entity entity : entities)
			entity.pulse();

		for (Entity entity : entities)
			entity.reset();
	}

	/**
	 * Gets the chunk manager.
	 * @return The chunk manager.
	 */
	public ChunkManager getChunks() {
		return chunks;
	}

	/**
	 * Gets the entity manager.
	 * @return The entity manager.
	 */
	public EntityManager getEntities() {
		return entities;
	}

	/**
	 * Gets a collection of all the players within this world.
	 * @return A {@link Collection} of {@link Player} objects.
	 */
	public Collection<Player> getPlayers() {
		return entities.getAll(Player.class);
	}

	/**
	 * Gets the spawn position.
	 * @return The spawn position.
	 */
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	/**
	 * Broadcats a message to every player.
	 * @param text The message text.
	 */
	public void broadcastMessage(String text) {
		ChatMessage message = new ChatMessage(text);
		for (Player player : getPlayers())
			player.getSession().send(message);
	}

}
