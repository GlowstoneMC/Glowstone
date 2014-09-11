package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.util.Position;
import org.apache.commons.lang.Validate;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.LinkedList;
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
        @Override
        protected String disambiguate(Entity subject, String metadataKey) {
            return subject.getUniqueId() + ":" + metadataKey;
        }
    }

    /**
     * The metadata store for entities.
     */
    private final static MetadataStore<Entity> bukkitMetadata = new EntityMetadataStore();

    /**
     * The server this entity belongs to.
     */
    protected final GlowServer server;

    /**
     * The entity's metadata.
     */
    protected final MetadataMap metadata = new MetadataMap(getClass());

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
    private UUID uuid;

    /**
     * This entity's current identifier for its world.
     */
    protected int id;

    /**
     * The current position.
     */
    protected final Location location;

    /**
     * The position in the last cycle.
     */
    protected final Location previousLocation;

    /**
     * The entity's velocity, applied each tick.
     */
    protected final Vector velocity = new Vector();

    /**
     * Whether the entity should have its position resent as if teleported.
     */
    protected boolean teleported = false;

    /**
     * Whether the entity should have its velocity resent.
     */
    protected boolean velocityChanged = false;

    /**
     * An EntityDamageEvent representing the last damage cause on this entity.
     */
    private EntityDamageEvent lastDamageCause;

    /**
     * A flag indicting if the entity is on the ground
     */
    private boolean onGround = true;

    /**
     * The distance the entity is currently falling without touching the ground.
     */
    private float fallDistance;

    /**
     * A counter of how long this entity has existed
     */
    private int ticksLived = 0;

    /**
     * How long the entity has been on fire, or 0 if it is not.
     */
    private int fireTicks = 0;

    /**
     * Creates an entity and adds it to the specified world.
     * @param location The location of the entity.
     */
    public GlowEntity(Location location) {
        this.location = location.clone();
        this.world = (GlowWorld) location.getWorld();
        this.server = world.getServer();
        world.getEntityManager().allocate(this);
        previousLocation = location.clone();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

    @Override
    public final GlowServer getServer() {
        return server;
    }

    @Override
    public final GlowWorld getWorld() {
        return world;
    }

    @Override
    public final int getEntityId() {
        return id;
    }

    @Override
    public UUID getUniqueId() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }

    @Override
    public boolean isDead() {
        return !active;
    }

    @Override
    public boolean isValid() {
        return world.getEntityManager().getEntity(id) == this;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Location stuff

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public Location getLocation(Location loc) {
        return Position.copyLocation(location, loc);
    }

    /**
     * Get the direction (SOUTH, WEST, NORTH, or EAST) this entity is facing.
     * @return The cardinal BlockFace of this entity.
     */
    public BlockFace getDirection() {
        double rot = getLocation().getYaw() % 360;
        if (rot < 0) {
            rot += 360.0;
        }
        if (0 <= rot && rot < 45) {
            return BlockFace.SOUTH;
        } else if (45 <= rot && rot < 135) {
            return BlockFace.WEST;
        } else if (135 <= rot && rot < 225) {
            return BlockFace.NORTH;
        } else if (225 <= rot && rot < 315) {
            return BlockFace.EAST;
        } else if (315 <= rot && rot < 360.0) {
            return BlockFace.SOUTH;
        } else {
            return BlockFace.EAST;
        }
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.velocity.copy(velocity);
        velocityChanged = true;
    }

    @Override
    public Vector getVelocity() {
        return velocity.clone();
    }

    @Override
    public boolean teleport(Location location) {
        if (location.getWorld() != world) {
            world.getEntityManager().deallocate(this);
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().allocate(this);
        }
        setRawLocation(location);
        teleported = true;
        return true;
    }

    @Override
    public boolean teleport(Entity destination) {
        return teleport(destination.getLocation());
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause) {
        return teleport(location);
    }

    @Override
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
        return isWithinDistance(other.location);
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
     * Checks whether this entity should be saved as part of the world.
     * @return True if the entity should be saved.
     */
    public boolean shouldSave() {
        return true;
    }

    /**
     * Called every game cycle. Subclasses should implement this to implement
     * periodic functionality e.g. mob AI.
     */
    public void pulse() {
        ticksLived++;

        if (fireTicks > 0) {
            --fireTicks;
        }
        metadata.setBit(MetadataIndex.STATUS, MetadataIndex.StatusFlags.ON_FIRE, fireTicks > 0);

        // resend position if it's been a while
        if (ticksLived % (30 * 20) == 0) {
            teleported = true;
        }
    }

    /**
     * Resets the previous location and other properties to their current value.
     */
    public void reset() {
        Position.copyLocation(location, previousLocation);
        metadata.resetChanges();
        teleported = false;
        velocityChanged = false;
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
        if (location.getWorld() != world) {
            throw new IllegalArgumentException("Cannot setRawLocation to a different world (got " + location.getWorld() + ", expected " + world + ")");
        }
        world.getEntityManager().move(this, location);
        Position.copyLocation(location, this.location);
    }

    /**
     * Sets this entity's unique identifier if possible.
     * @param uuid The new UUID. Must not be null.
     * @throws IllegalArgumentException if the passed UUID is null.
     * @throws IllegalStateException if a UUID has already been set.
     */
    public void setUniqueId(UUID uuid) {
        Validate.notNull(uuid, "uuid must not be null");
        if (this.uuid == null) {
            this.uuid = uuid;
        } else if (!this.uuid.equals(uuid)) {
            // silently allow setting the same UUID, since
            // it can't be checked with getUniqueId()
            throw new IllegalStateException("UUID of " + this + " is already " + this.uuid);
        }
    }

    /**
     * Creates a {@link Message} which can be sent to a client to spawn this
     * entity.
     * @return A message which can spawn this entity.
     */
    public abstract List<Message> createSpawnMessage();

    /**
     * Creates a {@link Message} which can be sent to a client to update this
     * entity.
     * @return A message which can update this entity.
     */
    public List<Message> createUpdateMessage() {
        boolean moved = hasMoved();
        boolean rotated = hasRotated();

        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int dx = x - Position.getIntX(previousLocation);
        int dy = y - Position.getIntY(previousLocation);
        int dz = z - Position.getIntZ(previousLocation);

        boolean teleport = dx > Byte.MAX_VALUE || dy > Byte.MAX_VALUE || dz > Byte.MAX_VALUE || dx < Byte.MIN_VALUE || dy < Byte.MIN_VALUE || dz < Byte.MIN_VALUE;

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        List<Message> result = new LinkedList<>();
        if (teleported || (moved && teleport)) {
            result.add(new EntityTeleportMessage(id, x, y, z, yaw, pitch));
        } else if (moved && rotated) {
            result.add(new RelativeEntityPositionRotationMessage(id, dx, dy, dz, yaw, pitch));
        } else if (moved) {
            result.add(new RelativeEntityPositionMessage(id, dx, dy, dz));
        } else if (rotated) {
            result.add(new EntityRotationMessage(id, yaw, pitch));
        }

        // todo: handle head rotation as a separate value
        if (rotated) {
            result.add(new EntityHeadRotationMessage(id, yaw));
        }

        // send changed metadata
        List<MetadataMap.Entry> changes = metadata.getChanges();
        if (changes.size() > 0) {
            result.add(new EntityMetadataMessage(id, changes));
        }

        // send velocity if needed
        if (velocityChanged) {
            result.add(new EntityVelocityMessage(id, velocity));
        }

        return result;
    }

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

    @Override
    public int getFireTicks() {
        return fireTicks;
    }

    @Override
    public void setFireTicks(int ticks) {
        fireTicks = ticks;
    }

    @Override
    public int getMaxFireTicks() {
        return 160;  // this appears to be Minecraft's default value
    }

    @Override
    public float getFallDistance() {
        return fallDistance;
    }

    @Override
    public void setFallDistance(float distance) {
        fallDistance = Math.max(distance, 0);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageCause = event;
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    @Override
    public int getTicksLived() {
        return ticksLived;
    }

    @Override
    public void setTicksLived(int value) {
        this.ticksLived = value;
    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Miscellaneous actions

    @Override
    public void remove() {
        active = false;
        world.getEntityManager().deallocate(this);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void playEffect(EntityEffect type) {

    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity stacking

    @Override
    public boolean isInsideVehicle() {
        return getVehicle() != null;
    }

    @Override
    public boolean leaveVehicle() {
        return false;
    }

    @Override
    public Entity getVehicle() {
        return null;
    }

    @Override
    public Entity getPassenger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean setPassenger(Entity passenger) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isEmpty() {
        return getPassenger() == null;
    }

    @Override
    public boolean eject() {
        return !isEmpty() && setPassenger(null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        bukkitMetadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return bukkitMetadata.getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return bukkitMetadata.hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        bukkitMetadata.removeMetadata(this, metadataKey, owningPlugin);
    }
}
