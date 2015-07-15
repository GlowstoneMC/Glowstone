package net.glowstone.entity;

import com.flowpowered.networking.Message;
import net.glowstone.EventFactory;
import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.physics.BoundingBox;
import net.glowstone.entity.physics.EntityBoundingBox;
import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.Position;
import org.apache.commons.lang3.Validate;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataStore;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 * @author Graham Edgecombe
 */
public abstract class GlowEntity implements Entity {

    /**
     * The metadata store class for entities.
     */
    private static final class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {
        @Override
        protected String disambiguate(Entity subject, String metadataKey) {
            return subject.getUniqueId() + ":" + metadataKey;
        }
    }

    /**
     * The metadata store for entities.
     */
    private static final MetadataStore<Entity> bukkitMetadata = new EntityMetadataStore();

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
     * The entity's bounding box, or null if it has no physical presence.
     */
    private EntityBoundingBox boundingBox;

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
    protected int ticksLived = 0;

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
        server.getEntityIdManager().allocate(this);
        world.getEntityManager().register(this);
        previousLocation = location.clone();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }


    ////////////////////////////////////////////////////////////////////////////
    // Command sender

    @Override
    public void sendMessage(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendMessage(String[] strings) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public final GlowServer getServer() {
        return server;
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

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

    /**
     * Gets the full direction (including SOUTH_SOUTH_EAST etc) this entity is facing.
     * @return The intercardinal BlockFace of this entity
     */
    public BlockFace getFacing() {
        long facing = Math.round(getLocation().getYaw() / 22.5) + 8;
        return Position.getDirection((byte) (facing % 16));
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
        Validate.notNull(location, "location cannot be null");
        Validate.notNull(location.getWorld(), "location's world cannot be null");

        if (location.getWorld() != world) {
            world.getEntityManager().unregister(this);
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().register(this);
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
        return !other.isDead() && (isWithinDistance(other.location) || other instanceof GlowLightningStrike);
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

        // resend position if it's been a while, causes ItemFrames to disappear.
        if (ticksLived % (30 * 20) == 0) {
            if (!(this instanceof GlowItemFrame)) {
                teleported = true;
            }
        }

        pulsePhysics();

        if (hasMoved()) {
            Block currentBlock = location.getBlock();
            if (currentBlock.getType() == Material.ENDER_PORTAL) {
                EventFactory.callEvent(new EntityPortalEnterEvent(this, currentBlock.getLocation()));
                if (server.getAllowEnd()) {
                    Location previousLocation = location.clone();
                    boolean success;
                    if (getWorld().getEnvironment() == World.Environment.THE_END) {
                        success = teleportToSpawn();
                    } else {
                        success = teleportToEnd();
                    }
                    if (success) {
                        EntityPortalExitEvent e = EventFactory.callEvent(new EntityPortalExitEvent(this, previousLocation, location.clone(), velocity.clone(), new Vector()));
                        if (!e.getAfter().equals(velocity)) {
                            setVelocity(e.getAfter());
                        }
                    }
                }
            }
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

    /**
     * Teleport this entity to the spawn point of the main world.
     * This is used to teleport out of the End.
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToSpawn() {
        Location target = server.getWorlds().get(0).getSpawnLocation();

        EntityPortalEvent event = EventFactory.callEvent(new EntityPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        teleport(target);
        return true;
    }

    /**
     * Teleport this entity to the End.
     * If no End world is loaded this does nothing.
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToEnd() {
        if (!server.getAllowEnd()) {
            return false;
        }
        Location target = null;
        for (World world : server.getWorlds()) {
            if (world.getEnvironment() == World.Environment.THE_END) {
                target = world.getSpawnLocation();
                break;
            }
        }
        if (target == null) {
            return false;
        }

        EntityPortalEvent event = EventFactory.callEvent(new EntityPortalEvent(this, location.clone(), target, null));
        if (event.isCancelled()) {
            return false;
        }
        target = event.getTo();

        teleport(target);
        return true;
    }

    protected void setSize(float xz, float y) {
        //todo Size stuff with bounding boxes.
    }

    /**
     * Determine if this entity is intersecting a block of the specified type.
     * If the entity has a defined bounding box, that is used to check for
     * intersection. Otherwise,
     * @param material The material to check for.
     * @return True if the entity is intersecting
     */
    public boolean isTouchingMaterial(Material material) {
        if (boundingBox == null) {
            // less accurate calculation if no bounding box is present
            for (BlockFace face : new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.DOWN, BlockFace.SELF,
                    BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST}) {
                if (getLocation().getBlock().getRelative(face).getType() == material) {
                    return true;
                }
            }
        } else {
            // bounding box-based calculation
            Vector min = boundingBox.minCorner, max = boundingBox.maxCorner;
            for (int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
                for (int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
                    for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
                        if (world.getBlockTypeIdAt(x, y, z) == material.getId()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Physics stuff

    protected final void setBoundingBox(double xz, double y) {
        boundingBox = new EntityBoundingBox(xz, y);
    }

    public boolean intersects(BoundingBox box) {
        return boundingBox != null && boundingBox.intersects(box);
    }

    protected void pulsePhysics() {
        // todo: update location based on velocity,
        // do gravity, all that other good stuff

        // make sure bounding box is up to date
        if (boundingBox != null) {
            boundingBox.setCenter(location.getX(), location.getY(), location.getZ());
        }
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
        world.getEntityManager().unregister(this);
        server.getEntityIdManager().deallocate(this);
    }

    @Override
    public List<Entity> getNearbyEntities(double x, double y, double z) {
        // This behavior is similar to CraftBukkit, where a call with args
        // (0, 0, 0) finds any entities whose bounding boxes intersect that of
        // this entity.

        BoundingBox searchBox;
        if (boundingBox == null) {
            searchBox = BoundingBox.fromPositionAndSize(location.toVector(), new Vector(0, 0, 0));
        } else {
            searchBox = BoundingBox.copyOf(boundingBox);
        }
        Vector vec = new Vector(x, y, z);
        searchBox.minCorner.subtract(vec);
        searchBox.maxCorner.add(vec);

        return world.getEntityManager().getEntitiesInside(searchBox, this);
    }

    @Override
    public void playEffect(EntityEffect type) {
        EntityStatusMessage message = new EntityStatusMessage(id, type);
        for (GlowPlayer player : world.getRawPlayers()) {
            if (player.canSeeEntity(this)) {
                player.getSession().send(message);
            }
        }
    }

    @Override
    public EntityType getType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public void setCustomName(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCustomName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setCustomNameVisible(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
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

    ////////////////////////////////////////////////////////////////////////////
    // Permissions

    @Override
    public boolean isPermissionSet(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(String s) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void recalculatePermissions() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isOp() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOp(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        // Override in subclasses to implement behavior
        return false;
    }

    public Entity.Spigot spigot() {
        return null; // TODO: support entity isInvulnerable() API
    }
}
