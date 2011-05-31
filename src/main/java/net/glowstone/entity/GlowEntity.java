package net.glowstone.entity;

import net.glowstone.GlowChunk;
import net.glowstone.util.Position;
import org.bukkit.Location;

import net.glowstone.msg.Message;
import net.glowstone.GlowWorld;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 * @author Graham Edgecombe
 */
public abstract class GlowEntity {

    /**
     * The world this entity belongs to.
     */
	protected final GlowWorld world;

    /**
     * A flag indicating if this entity is currently active.
     */
	protected boolean active = true;

    /**
     * This entity's unique id.
     */
	protected int id;

    /**
     * The current position.
     */
	protected Location location = Position.ZERO;

    /**
     * The position in the last cycle.
     */
	protected Location previousLocation = Position.ZERO;

    /**
     * Creates an entity and adds it to the specified world.
     * @param world The world.
     */
	public GlowEntity(GlowWorld world) {
		this.world = world;
		world.getEntityManager().allocate(this);
	}

    /**
     * Checks if this entity is within the {@link GlowChunk#VISIBLE_RADIUS} of
     * another.
     * @param other The other entity.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
	public boolean isWithinDistance(GlowEntity other) {
		double dx = Math.abs(location.getX() - other.location.getX());
		double dz = Math.abs(location.getZ() - other.location.getZ());
		return dx <= (GlowChunk.VISIBLE_RADIUS * GlowChunk.WIDTH) && dz <= (GlowChunk.VISIBLE_RADIUS * GlowChunk.HEIGHT);
	}

    /**
     * Gets the world this entity is in.
     * @return The world this entity is in.
     */
	public GlowWorld getWorld() {
		return world;
	}

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
	public void destroy() {
		active = false;
		world.getEntityManager().deallocate(this);
	}

    /**
     * Checks if this entity is active.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean isActive() {
		return active;
	}

    /**
     * Gets the id of this entity.
     * @return The id.
     */
	public int getId() {
		return id;
	}

    /**
     * Called every game cycle. Subclasses should implement this to implement
     * periodic functionality e.g. mob AI.
     */
	public void pulse() {

	}

    /**
     * Resets the previous position and rotations of the entity to the current
     * position and rotation.
     */
	public void reset() {
		previousLocation = location;
	}

    /**
     * Gets this entity's position.
     * @return The position of this entity.
     */
	public Location getLocation() {
		return location;
	}

    /**
     * Gets the entity's previous position.
     * @return The previous position of this entity.
     */
	public Location getPreviousLocation() {
		return previousLocation;
	}

    /**
     * Sets this entity's position.
     * @param position The new position.
     */
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlowEntity other = (GlowEntity) obj;
		if (id != other.id)
			return false;
		return true;
	}

    /**
     * Creates a {@link Message} which can be sent to a client to spawn this
     * entity.
     * @return A message which can spawn this entity.
     */
	public abstract Message createSpawnMessage();

    /**
     * Creates a {@link Message} which can be sent to a client to update this
     * entity.
     * @return A message which can update this entity.
     */
	public abstract Message createUpdateMessage();

    /**
     * Checks if this entity has moved this cycle.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean hasMoved() {
		return Position.hasMoved(location, previousLocation);
	}

    /**
     * Checks if this entity has rotated this cycle.
     * @return {@code true} if so, {@code false} if not.
     */
	public boolean hasRotated() {
		return Position.hasRotated(location, previousLocation);
	}

}
