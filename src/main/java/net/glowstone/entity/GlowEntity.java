package net.glowstone.entity;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.msg.Message;
import net.glowstone.util.Position;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 * @author Graham Edgecombe
 */
public abstract class GlowEntity implements Entity {

    /**
     * The metadata store class for entities.
     */
    private final static class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {
        protected String disambiguate(Entity subject, String metadataKey) {
            return subject.getUniqueId() + ":" + metadataKey;
        }
    }

    /**
     * The metadata store for entities.
     */
    private final static MetadataStore<Entity> metadata = new EntityMetadataStore();
    
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

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    public final GlowServer getServer() {
        return server;
    }

    public final GlowWorld getWorld() {
        return world;
    }

    public final int getEntityId() {
        return id;
    }

    public UUID getUniqueId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDead() {
        return !active;
    }

    public boolean isValid() {
        return world.getEntityManager().getEntity(id) == this;
    }

    public EntityType getType() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Location stuff

    public Location getLocation() {
        return location.clone();
    }

    public Location getLocation(Location loc) {
        if (loc == null) return null;
        loc.setX(location.getX());
        loc.setY(location.getY());
        loc.setZ(location.getZ());
        loc.setPitch(location.getPitch());
        loc.setYaw(location.getYaw());
        return loc;
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

    public boolean teleport(Location location, TeleportCause cause) {
        return teleport(location);
    }

    public boolean teleport(Entity destination, TeleportCause cause) {
        return teleport(destination.getLocation(), cause);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Checks if this entity is within the visible radius of another.
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
     * Checks if this entity is within the visible radius of a location.
     * @param loc The location.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
    public boolean isWithinDistance(Location loc) {
        double dx = Math.abs(location.getX() - loc.getX());
        double dz = Math.abs(location.getZ() - loc.getZ());
        return loc.getWorld() == getWorld() && dx <= (server.getViewDistance() * GlowChunk.WIDTH) && dz <= (server.getViewDistance() * GlowChunk.HEIGHT);
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

    ////////////////////////////////////////////////////////////////////////////
    // Various properties

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

    ////////////////////////////////////////////////////////////////////////////
    // Miscellaneous actions

    public void remove() {
        active = false;
        world.getEntityManager().deallocate(this);
    }

    public List<Entity> getNearbyEntities(double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void playEffect(EntityEffect type) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity stacking

    public boolean isInsideVehicle() {
        return getVehicle() == null;
    }

    public boolean leaveVehicle() {
        return false;
    }

    public Entity getVehicle() {
        return null;
    }

    public Entity getPassenger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setPassenger(Entity passenger) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty() {
        return getPassenger() == null;
    }

    public boolean eject() {
        return !isEmpty() && setPassenger(null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        metadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        return metadata.getMetadata(this, metadataKey);
    }

    public boolean hasMetadata(String metadataKey) {
        return metadata.hasMetadata(this, metadataKey);
    }

    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        metadata.removeMetadata(this, metadataKey, owningPlugin);
    }
}
