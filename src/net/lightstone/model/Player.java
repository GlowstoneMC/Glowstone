package net.lightstone.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.lightstone.msg.DestroyEntityMessage;
import net.lightstone.msg.LoadChunkMessage;
import net.lightstone.msg.Message;
import net.lightstone.msg.PingMessage;
import net.lightstone.msg.PositionRotationMessage;
import net.lightstone.msg.SpawnPlayerMessage;
import net.lightstone.net.Session;

/**
 * Represents an in-game player.
 * @author Graham Edgecombe
 */
public final class Player extends Mob {

    /**
     * The normal height of a player's eyes above their feet.
     */
	public static final double EYE_HEIGHT = 1.62D;

    /**
     * The name of this player.
     */
	private final String name;

    /**
     * This player's session.
     */
	private final Session session;

    /**
     * The entities that the client knows about.
     */
	private Set<Entity> knownEntities = new HashSet<Entity>();

    /**
     * The chunks that the client knows about.
     */
	private Set<Chunk.Key> knownChunks = new HashSet<Chunk.Key>();

    /**
     * Creates a new player and adds it to the world.
     * @param session The player's session.
     * @param name The player's name.
     */
	public Player(Session session, String name) {
		super(session.getServer().getWorld());
		this.name = name;
		this.session = session;

		// stream the initial set of blocks and teleport us
		this.streamBlocks();
		this.position = world.getSpawnPosition();
		this.session.send(new PositionRotationMessage(position.getX(), position.getY(), position.getZ(), position.getY() + EYE_HEIGHT, (float) rotation.getYaw(), (float) rotation.getPitch(), true));
	}

    /**
     * Gets the name of this player.
     * @return The name of this player.
     */
	public String getName() {
		return name;
	}

	@Override
	public void pulse() {
		super.pulse();
        session.send(new PingMessage());

		streamBlocks();

		for (Iterator<Entity> it = knownEntities.iterator(); it.hasNext(); ) {
			Entity entity = it.next();
			boolean withinDistance = entity.isActive() && isWithinDistance(entity);

			if (withinDistance) {
				Message msg = entity.createUpdateMessage();
				if (msg != null)
					session.send(msg);
			} else {
				session.send(new DestroyEntityMessage(entity.getId()));
				it.remove();
			}
		}

		for (Entity entity : world.getEntities()) {
			if (entity == this)
				continue;
			boolean withinDistance = entity.isActive() && isWithinDistance(entity);

			if (withinDistance && !knownEntities.contains(entity)) {
				knownEntities.add(entity);
				session.send(entity.createSpawnMessage());
			}
		}
	}

    /**
     * Streams chunks to the player's client.
     */
	private void streamBlocks() {
        Set<Chunk.Key> previousChunks = new HashSet<Chunk.Key>(knownChunks);

		int centralX = ((int) position.getX()) / Chunk.WIDTH;
		int centralZ = ((int) position.getZ()) / Chunk.HEIGHT;

		for (int x = (centralX - Chunk.VISIBLE_RADIUS); x <= (centralX + Chunk.VISIBLE_RADIUS); x++) {
			for (int z = (centralZ - Chunk.VISIBLE_RADIUS); z <= (centralZ + Chunk.VISIBLE_RADIUS); z++) {
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
			session.send(new LoadChunkMessage(key.getX(), key.getZ(), false));
			knownChunks.remove(key);
		}

		previousChunks.clear();
	}

    /**
     * Gets the session.
     * @return The session.
     */
	public Session getSession() {
		return session;
	}

	@Override
	public Message createSpawnMessage() {
		int x = position.getIntX();
		int y = position.getIntY();
		int z = position.getIntZ();
		int yaw = rotation.getIntYaw();
		int pitch = rotation.getIntPitch();
		return new SpawnPlayerMessage(id, name, x, y, z, yaw, pitch, 0);
	}

}
