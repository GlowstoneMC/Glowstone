package net.glowstone.entity;

import java.util.List;
import java.util.UUID;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;
import org.bukkit.Location;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.util.Position;

import net.glowstone.msg.Message;
import net.glowstone.GlowWorld;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 * @author Graham Edgecombe
 */
public abstract class GlowEntity implements Entity {
    
    /**
     * The server this entity belongs to.
     */
    protected final GlowServer server;

    /**
     * The world this entity belongs to.
     */
    protected GlowWorld world;

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
     * An EntityDamageEvent representing the last damage cause on this entity.
     */
    private EntityDamageEvent lastDamageCause;

    /**
     * A flag indicting if the entity is on the ground
     */
    private boolean onGround = true;

    /**
     * A counter of how long this entity has existed
     */
    private int ticksLived = 0;
    /**
     * Creates an entity and adds it to the specified world.
     * @param world The world.
     */
    public GlowEntity(GlowServer server, GlowWorld world) {
        this.server = server;
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
        return other.getWorld() == getWorld() && dx <= (server.getViewDistance() * GlowChunk.WIDTH) && dz <= (server.getViewDistance() * GlowChunk.HEIGHT);
    }

    /**
     * Checks if this entity is within the {@link GlowChunk#VISIBLE_RADIUS} of
     * a location.
     * @param loc The location.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
    public boolean isWithinDistance(Location loc) {
        double dx = Math.abs(location.getX() - loc.getX());
        double dz = Math.abs(location.getZ() - loc.getZ());
        return loc.getWorld() == getWorld() && dx <= (GlowChunk.VISIBLE_RADIUS * GlowChunk.WIDTH) && dz <= (GlowChunk.VISIBLE_RADIUS * GlowChunk.HEIGHT);
    }

    /**
     * Gets the world this entity is in.
     * @return The world this entity is in.
     */
    public GlowWorld getWorld() {
        return world;
    }

    /**
     * Gets the {@link org.bukkit.Server} that contains this Entity
     *
     * @return Server instance running this Entity
     */
    public GlowServer getServer() {
        return server;
    }

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    public void remove() {
        active = false;
        world.getEntityManager().deallocate(this);
    }

    /**
     * Checks if this entity is inactive.
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isDead() {
        return !active;
    }

    /**
     * Gets the id of this entity.
     * @return The id.
     */
    public int getEntityId() {
        return id;
    }

    /**
     * Called every game cycle. Subclasses should implement this to implement
     * periodic functionality e.g. mob AI.
     */
    public void pulse() {
        ticksLived++;
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
        return location.clone();
    }

    /**
     * Gets the entity's previous position.
     * @return The previous position of this entity.
     */
    public Location getPreviousLocation() {
        return previousLocation;
    }

    /**
     * Sets this entity's location.
     * @param location The new location.
     */
    public void setRawLocation(Location location) {
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

    
    public void setVelocity(Vector velocity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Vector getVelocity() {
        return location.toVector().subtract(previousLocation.toVector());
    }

    public boolean teleport(Location location) {
        if (location.getWorld() != world) {
            world.getEntityManager().deallocate(this);
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);
        }
        this.location = location;
        reset();
        return true;
    }

    public boolean teleport(Entity destination) {
        return teleport(destination.getLocation());
    }

    public List<Entity> getNearbyEntities(double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getFireTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaxFireTicks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFireTicks(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getRemainingAir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setRemainingAir(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMaximumAir() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setMaximumAir(int ticks) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Entity getPassenger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setPassenger(Entity passenger) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean eject() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getFallDistance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFallDistance(float distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageCause = event;
    }

    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    public UUID getUniqueId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getTicksLived() {
        return ticksLived;
    }

    public void setTicksLived(int value) {
        this.ticksLived = value;
    }

    public boolean isOnGround() {
        return onGround;
    }
    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

}
