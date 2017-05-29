package net.glowstone.entity;

import com.flowpowered.network.Message;
import com.google.common.base.Preconditions;
import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataIndex.StatusFlags;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.entity.meta.MetadataMap.Entry;
import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.entity.physics.BoundingBox;
import net.glowstone.entity.physics.EntityBoundingBox;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.Position;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represents some entity in the world such as an item on the floor or a player.
 *
 * @author Graham Edgecombe
 */
public abstract class GlowEntity implements Entity {

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
     * The world this entity belongs to.
     */
    protected GlowWorld world;
    /**
     * A flag indicating if this entity is currently active.
     */
    protected boolean active = true;
    /**
     * This entity's current identifier for its world.
     */
    protected int id;
    /**
     * Whether the entity should have its position resent as if teleported.
     */
    protected boolean teleported;
    /**
     * Whether the entity should have its velocity resent.
     */
    protected boolean velocityChanged;
    /**
     * A counter of how long this entity has existed
     */
    protected int ticksLived;
    /**
     * Vehicle
     */
    protected GlowEntity vehicle;
    /**
     * This entity's unique id.
     */
    private UUID uuid;
    /**
     * The entity's bounding box, or null if it has no physical presence.
     */
    protected EntityBoundingBox boundingBox;
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
     * How long the entity has been on fire, or 0 if it is not.
     */
    private int fireTicks;
    /**
     * Passenger
     */
    private GlowEntity passenger;
    protected boolean passengerChanged;
    /**
     * Whether gravity applies to the entity.
     */
    private boolean gravity = true;
    /**
     * Whether this entity is invulnerable.
     */
    private boolean invulnerable;
    /**
     * Whether this entity was forcibly removed from the world.
     */
    @Getter
    protected boolean removed;

    /**
     * The original location of this entity.
     */
    @Getter
    private final Location origin;

    /**
     * Creates an entity and adds it to the specified world.
     *
     * @param location The location of the entity.
     */
    public GlowEntity(Location location) {
        // this is so dirty I washed my hands after writing it.
        if (this instanceof GlowPlayer) {
            // spawn location event
            location = EventFactory.callEvent(new PlayerSpawnLocationEvent((Player) this, location)).getSpawnLocation();
        }
        this.origin = location.clone();
        this.location = location.clone();
        world = (GlowWorld) location.getWorld();
        server = world.getServer();
        server.getEntityIdManager().allocate(this);
        world.getEntityManager().register(this);
        previousLocation = location.clone();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void sendMessage(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    ////////////////////////////////////////////////////////////////////////////
    // Command sender

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

    @Override
    public final GlowWorld getWorld() {
        return world;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Core properties

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

    /**
     * Sets this entity's unique identifier if possible.
     *
     * @param uuid The new UUID. Must not be null.
     * @throws IllegalArgumentException if the passed UUID is null.
     * @throws IllegalStateException    if a UUID has already been set.
     */
    public void setUniqueId(UUID uuid) {
        checkNotNull(uuid, "uuid must not be null");
        if (this.uuid == null) {
            this.uuid = uuid;
        } else if (!this.uuid.equals(uuid)) {
            // silently allow setting the same UUID, since
            // it can't be checked with getUniqueId()
            throw new IllegalStateException("UUID of " + this + " is already " + this.uuid);
        }
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
     *
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
     *
     * @return The intercardinal BlockFace of this entity
     */
    public BlockFace getFacing() {
        long facing = Math.round(getLocation().getYaw() / 22.5) + 8;
        return Position.getDirection((byte) (facing % 16));
    }

    @Override
    public Vector getVelocity() {
        return velocity.clone();
    }

    @Override
    public void setVelocity(Vector velocity) {
        this.velocity.copy(velocity);
        velocityChanged = true;
    }

    public void setGravityAccel(Vector gravity) {
        this.gravityAccel = gravity;
    }

    public Vector getGravityAccel() {
        if (this.gravity) {
            return this.gravityAccel;
        } else {
            return this.zeroG;
        }
    }

    public void setDrag(double drag, boolean liquid) {
        if (liquid) {
            liquidDrag = drag;
        } else {
            airDrag = drag;
        }
    }

    @Override
    public boolean teleport(Location location) {
        checkNotNull(location, "location cannot be null");
        checkNotNull(location.getWorld(), "location's world cannot be null");

        if (location.getWorld() != world) {
            world.getEntityManager().unregister(this);
            world = (GlowWorld) location.getWorld();
            world.getEntityManager().register(this);
        }
        setRawLocation(location, false);
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

    public boolean isTeleported() {
        return teleported;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Checks if this entity is within the visible radius of another.
     *
     * @param other The other entity.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
    public boolean isWithinDistance(GlowEntity other) {
        if (other instanceof GlowLivingEntity) {
            return ((GlowLivingEntity) other).getDeathTicks() <= 20 && isWithinDistance(other.location);
        } else return !other.isDead() && (isWithinDistance(other.location) || other instanceof GlowLightningStrike);
    }

    /**
     * Checks if this entity is within the visible radius of a location.
     *
     * @param loc The location.
     * @return {@code true} if the entities can see each other, {@code false} if
     * not.
     */
    public boolean isWithinDistance(Location loc) {
        double dx = Math.abs(location.getX() - loc.getX());
        double dz = Math.abs(location.getZ() - loc.getZ());
        return loc.getWorld() == getWorld() && dx <= server.getViewDistance() * GlowChunk.WIDTH && dz <= server.getViewDistance() * GlowChunk.HEIGHT;
    }

    /**
     * Checks whether this entity should be saved as part of the world.
     *
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
        if (!getLocation().getChunk().isLoaded()) {
            return;
        }

        if (fireTicks > 0) {
            --fireTicks;
        }
        metadata.setBit(MetadataIndex.STATUS, StatusFlags.ON_FIRE, fireTicks > 0);

        // resend position if it's been a while, causes ItemFrames to disappear.
        if (ticksLived % (30 * 20) == 0) {
            if (!(this instanceof GlowItemFrame)) {
                teleported = true;
            }
        }

        if (this instanceof GlowLivingEntity && !isDead() && ((GlowLivingEntity) this).hasAI() && this.getLocation().getChunk().isLoaded()) {
            GlowLivingEntity entity = (GlowLivingEntity) this;
            entity.getTaskManager().pulse();
        }

        pulsePhysics();

        if (hasMoved()) {
            Block currentBlock = location.getBlock();
            if (currentBlock.getType() == Material.ENDER_PORTAL) {
                EventFactory.callEvent(new EntityPortalEnterEvent(this, currentBlock.getLocation()));
                if (server.getAllowEnd()) {
                    Location previousLocation = location.clone();
                    boolean success;
                    if (getWorld().getEnvironment() == Environment.THE_END) {
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
        passengerChanged = false;
    }

    /**
     * Sets this entity's location.
     *
     * @param location The new location.
     * @param fall     Whether to calculate fall damage or not.
     */
    public void setRawLocation(Location location, boolean fall) {
        if (location.getWorld() != world) {
            throw new IllegalArgumentException("Cannot setRawLocation to a different world (got " + location.getWorld() + ", expected " + world + ")");
        }

        if (Objects.equals(location, previousLocation)) {
            return;
        }

        if (teleported) {
            teleported = false;
        }

        world.getEntityManager().move(this, location);
        Position.copyLocation(location, this.location);

        updateBoundingBox();

        Material type = location.getBlock().getType();

        if (!fall || type == Material.LADDER // todo: horses are not affected
                || type == Material.VINE // todo: horses are not affected
                || type == Material.WATER
                || type == Material.STATIONARY_WATER
                || type == Material.WEB
                || type == Material.TRAP_DOOR
                || type == Material.IRON_TRAPDOOR
                || onGround) {
            fallDistance = 0;
        } else if (location.getY() < previousLocation.getY() && !isInsideVehicle()) {
            fallDistance += previousLocation.getY() - location.getY();
        }

        if (fall && !(this instanceof GlowPlayer)) {
            setOnGround(location.clone().add(new Vector(0, -1, 0)).getBlock().getType().isSolid());
        }
    }

    /**
     * Sets this entity's location and applies fall damage calculations.
     *
     * @param location The new location.
     */
    public void setRawLocation(Location location) {
        setRawLocation(location, true);
    }

    /**
     * Creates a {@link Message} which can be sent to a client to spawn this
     * entity.
     *
     * @return A message which can spawn this entity.
     */
    public abstract List<Message> createSpawnMessage();

    /**
     * Creates a {@link Message} which can be sent to a client to update this
     * entity.
     *
     * @return A message which can update this entity.
     */
    public List<Message> createUpdateMessage() {
        boolean moved = hasMoved();
        boolean rotated = hasRotated();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double dx = x * 32 - previousLocation.getX() * 32;
        double dy = y * 32 - previousLocation.getY() * 32;
        double dz = z * 32 - previousLocation.getZ() * 32;

        dx *= 128;
        dy *= 128;
        dz *= 128;

        boolean teleport = dx > Short.MAX_VALUE || dy > Short.MAX_VALUE || dz > Short.MAX_VALUE || dx < Short.MIN_VALUE || dy < Short.MIN_VALUE || dz < Short.MIN_VALUE;

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        List<Message> result = new LinkedList<>();
        if (teleported || moved && teleport) {
            result.add(new EntityTeleportMessage(id, x, y, z, yaw, pitch));
        } else if (moved && rotated) {
            result.add(new RelativeEntityPositionRotationMessage(id, (short) dx, (short) dy, (short) dz, yaw, pitch));
        } else if (moved) {
            result.add(new RelativeEntityPositionMessage(id, (short) dx, (short) dy, (short) dz));
        } else if (rotated) {
            result.add(new EntityRotationMessage(id, yaw, pitch));
        }

        // send changed metadata
        List<Entry> changes = metadata.getChanges();
        if (!changes.isEmpty()) {
            result.add(new EntityMetadataMessage(id, changes));
        }

        // send velocity if needed
        if (velocityChanged) {
            result.add(new EntityVelocityMessage(id, velocity));
        }

        if (passengerChanged) {
            //this method will not call for this player, we don't need check SELF_ID
            result.add(new SetPassengerMessage(getEntityId(), getPassengers().stream().mapToInt(Entity::getEntityId).toArray()));
        }

        return result;
    }

    /**
     * Checks if this entity has moved this cycle.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasMoved() {
        return Position.hasMoved(location, previousLocation);
    }

    /**
     * Checks if this entity has rotated this cycle.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean hasRotated() {
        return Position.hasRotated(location, previousLocation);
    }

    /**
     * Teleport this entity to the spawn point of the main world.
     * This is used to teleport out of the End.
     *
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
     *
     * @return {@code true} if the teleport was successful.
     */
    protected boolean teleportToEnd() {
        if (!server.getAllowEnd()) {
            return false;
        }
        Location target = null;
        for (World world : server.getWorlds()) {
            if (world.getEnvironment() == Environment.THE_END) {
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
        setBoundingBox(xz, y);
    }

    /**
     * Determine if this entity is intersecting a block of the specified type.
     * If the entity has a defined bounding box, that is used to check for
     * intersection. Otherwise,
     *
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

    protected final void setBoundingBox(double xz, double y) {
        boundingBox = new EntityBoundingBox(xz, y);
        updateBoundingBox();
    }

    @Override
    public double getWidth() {
        return boundingBox.getSize().getX();
    }

    @Override
    public double getHeight() {
        return boundingBox.getSize().getY();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Physics stuff

    public boolean intersects(BoundingBox box) {
        return boundingBox != null && boundingBox.intersects(box);
    }

    /**
     * Velocity reduction applied each tick in air.
     */
    protected double airDrag = 0.98;

    /**
     * Velocity reduction applied each tick in liquids.
     */
    protected double liquidDrag = 0.8;

    /**
     * Gravity acceleration applied each tick.
     */
    protected Vector gravityAccel = new Vector(0, -0.04, 0);
    private static final Vector zeroG = new Vector();
    /**
     * The slipperiness multiplier applied according to the block this entity was on.
     */
    protected double slipMultiplier = 0.6;

    protected void pulsePhysics() {
        if (velocity.lengthSquared() > 0.01) {
            double dx = 0;
            double dy = 0;
            double dz = 0;

            double ndx = 0;
            double ndy = 0;
            double ndz = 0;

            double x = location.getX();
            double y = location.getY();
            double z = location.getZ();

            Vector inc = velocity.clone().normalize();

            BoundingBox test;
            boolean bbRelevant = (boundingBox == null ? 0 : boundingBox.getSize().lengthSquared()) >= 1;

            if (velocity.getY() < 0d) {
                if (!location.clone().add(new Vector(0, -1, 0)).getBlock().getType().isSolid()) {
                    block:
                    while (dy >= velocity.getY()) {
                        ndy += inc.getY();
                        if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) x, (int) (y + ndy), (int) z)).isSolid()) {
                            break;
                        }
                        if (bbRelevant) {
                            test = BoundingBox.fromPositionAndSize(new Vector((int) x, (int) (y + ndy), (int) z), boundingBox.getSize());
                            Vector min = test.minCorner, max = test.maxCorner;
                            for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                                for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                    for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                        if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                            break block;
                                        }
                                    }
                                }
                            }
                        }
                        dy = ndy;
                    }
                }
            } else if (velocity.getY() > 0d) {
                block:
                while (dy <= velocity.getY()) {
                    ndy += inc.getY();
                    if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) x, (int) (y + ndy), (int) z)).isSolid()) {
                        break;
                    }
                    if (bbRelevant) {
                        test = BoundingBox.fromPositionAndSize(new Vector((int) x, (int) (y + ndy), (int) z), boundingBox.getSize());
                        Vector min = test.minCorner, max = test.maxCorner;
                        for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                            for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                    if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                    dy = ndy;
                }
            }
            velocity.setY(dy);

            if (velocity.getX() < 0d) {
                block:
                while (dx >= velocity.getX()) {
                    ndx += inc.getX();
                    if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) (x + ndx), (int) (y + dy), (int) z)).isSolid()) {
                        break;
                    }
                    if (bbRelevant) {
                        test = BoundingBox.fromPositionAndSize(new Vector((int) (x + ndx), (int) (y + dy), (int) z), boundingBox.getSize());
                        Vector min = test.minCorner, max = test.maxCorner;
                        for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                            for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                    if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                    dx = ndx;
                }
            } else if (velocity.getX() > 0d) {
                block:
                while (dx <= velocity.getX()) {
                    ndx += inc.getX();
                    if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) (x + ndx), (int) (y + dy), (int) z)).isSolid()) {
                        break;
                    }
                    if (bbRelevant) {
                        test = BoundingBox.fromPositionAndSize(new Vector((int) (x + ndx), (int) (y + dy), (int) z), boundingBox.getSize());
                        Vector min = test.minCorner, max = test.maxCorner;
                        for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                            for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                    if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                    dx = ndx;
                }
            }
            velocity.setX(dx);

            if (velocity.getZ() < 0d) {
                block:
                while (dz >= velocity.getZ()) {
                    ndz += inc.getZ();
                    if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) (x + dx), (int) (y + dy), (int) (z + ndz))).isSolid()) {
                        break;
                    }
                    if (bbRelevant) {
                        test = BoundingBox.fromPositionAndSize(new Vector((int) (x + dx), (int) (y + dy), (int) (z + ndz)), boundingBox.getSize());
                        Vector min = test.minCorner, max = test.maxCorner;
                        for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                            for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                    if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                    dz = ndz;
                }
            } else if (velocity.getZ() > 0d) {
                block:
                while (dz <= velocity.getZ()) {
                    ndz += inc.getZ();
                    if (Material.getMaterial(((GlowWorld) location.getWorld()).getBlockTypeIdAt((int) (x + dx), (int) (y + dy), (int) (z + ndz))).isSolid()) {
                        break;
                    }
                    if (bbRelevant) {
                        test = BoundingBox.fromPositionAndSize(new Vector((int) (x + dx), (int) (y + dy), (int) (z + ndz)), boundingBox.getSize());
                        Vector min = test.minCorner, max = test.maxCorner;
                        for (int bbx = min.getBlockX(); bbx <= max.getBlockX(); ++bbx) {
                            for (int bby = min.getBlockY(); bby <= max.getBlockY(); ++bby) {
                                for (int bbz = min.getBlockZ(); bbz <= max.getBlockZ(); ++bbz) {
                                    if (Material.getMaterial(world.getBlockTypeIdAt(bbx, bby, bbz)).isSolid()) {
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                    dz = ndz;
                }
            }
            velocity.setZ(dz);

            setRawLocation(location.clone().add(velocity));
        }

        // apply friction and gravity
        if (location.getBlock().getType() == Material.WATER) {
            velocity.multiply(liquidDrag);
            velocity.setY(velocity.getY() + getGravityAccel().getY() / 4d);
        } else if (location.getBlock().getType() == Material.LAVA) {
            velocity.multiply(liquidDrag - 0.3);
            velocity.setY(velocity.getY() + getGravityAccel().getY() / 4d);
        } else {
            velocity.setY(airDrag * (velocity.getY() + getGravityAccel().getY()));
            if (isOnGround()) {
                velocity.setX(velocity.getX() * slipMultiplier);
                velocity.setZ(velocity.getZ() * slipMultiplier);
            } else {
                velocity.setX(velocity.getX() * 0.91);
                velocity.setZ(velocity.getZ() * 0.91);
            }
        }
    }

    protected void updateBoundingBox() {
        // make sure bounding box is up to date
        if (boundingBox != null) {
            boundingBox.setCenter(location.getX(), location.getY(), location.getZ());
        }
    }

    @Override
    public int getFireTicks() {
        return fireTicks;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Various properties

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
    public EntityDamageEvent getLastDamageCause() {
        return lastDamageCause;
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent event) {
        lastDamageCause = event;
    }

    @Override
    public int getTicksLived() {
        return ticksLived;
    }

    @Override
    public void setTicksLived(int value) {
        ticksLived = value;
    }

    @Override
    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        if (onGround) {
            setFallDistance(0);
        }
        this.onGround = onGround;
    }

    /**
     * Destroys this entity by removing it from the world and marking it as not
     * being active.
     */
    @Override
    public void remove() {
        removed = true;
        active = false;
        boundingBox = null;
        world.getEntityManager().unregister(this);
        server.getEntityIdManager().deallocate(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Miscellaneous actions

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
        world.getRawPlayers().stream().filter(player -> player.canSeeEntity(this)).forEach(player -> player.getSession().send(message));
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    @Override
    public boolean isInsideVehicle() {
        return getVehicle() != null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Entity stacking

    @Override
    public boolean leaveVehicle() {
        return isInsideVehicle() && vehicle.setPassenger(null); // TODO: multiple passengers
    }

    @Override
    public Entity getVehicle() {
        return vehicle;
    }

    @Override
    public String getCustomName() {
        String name = metadata.getString(MetadataIndex.NAME_TAG);
        if (name == null || name.isEmpty()) {
            name = "";
        }
        return name;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Custom name

    @Override
    public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        metadata.set(MetadataIndex.NAME_TAG, name); // remove ?
    }

    @Override
    public boolean isCustomNameVisible() {
        return metadata.getBoolean(MetadataIndex.SHOW_NAME_TAG);
    }

    @Override
    public void setGlowing(boolean glowing) {
        metadata.setBit(MetadataIndex.STATUS, StatusFlags.GLOWING, glowing);
    }

    @Override
    public boolean isGlowing() {
        return metadata.getBit(MetadataIndex.STATUS, StatusFlags.GLOWING);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    @Override
    public boolean isInvulnerable() {
        return invulnerable;
    }

    @Override
    public void setCustomNameVisible(boolean flag) {
        metadata.set(MetadataIndex.SHOW_NAME_TAG, flag);
    }

    @Override
    public Entity getPassenger() {
        return passenger;
    }

    @Override
    public List<Entity> getPassengers() {
        return Arrays.asList(new Entity[]{passenger}); // TODO: multiple passengers
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        return passenger.leaveVehicle();
    }

    @Override
    public boolean addPassenger(Entity passenger) {
        return setPassenger(passenger); // TODO: multiple passengers
    }

    @Override
    public boolean setPassenger(Entity bPassenger) {
        Preconditions.checkArgument(bPassenger != this, "Entity cannot ride itself.");

        if (passenger == bPassenger) return false; // nothing changed

        if (bPassenger == null) {

            EventFactory.callEvent(new EntityDismountEvent(passenger, this));

            passengerChanged = true;
            passenger.vehicle = null;
            passenger = null;
        } else {

            if (!(bPassenger instanceof GlowEntity)) {
                return false;
            }

            GlowEntity passenger = (GlowEntity) bPassenger;

            if (passenger.vehicle != null) {
                EventFactory.callEvent(new EntityDismountEvent(passenger, passenger.vehicle));
                passenger.vehicle.passenger = null;
                passenger.vehicle = null;
            }

            EntityMountEvent event = new EntityMountEvent(passenger, this);
            EventFactory.callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            if (this.passenger != null) {
                EventFactory.callEvent(new EntityDismountEvent(this.passenger, this));
                this.passengerChanged = true;
                this.passenger.vehicle = null;
            }

            this.passenger = passenger;
            this.passenger.vehicle = this;
            this.passengerChanged = true;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return getPassenger() == null;
    }

    @Override
    public boolean eject() {
        return !isEmpty() && setPassenger(null);
    }

    @Override
    public boolean hasGravity() {
        return gravity;
    }

    @Override
    public void setGravity(boolean gravity) {
        this.gravity = gravity;
    }

    @Override
    public int getPortalCooldown() {
        // todo: 1.11
        return 0;
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        // todo: 1.11
    }

    @Override
    public Set<String> getScoreboardTags() {
        // todo: 1.11
        return null;
    }

    @Override
    public boolean addScoreboardTag(String tag) {
        // todo: 1.11
        return false;
    }

    @Override
    public boolean removeScoreboardTag(String tag) {
        // todo: 1.11
        return false;
    }

    @Override
    public boolean isSilent() {
        return metadata.getBoolean(MetadataIndex.SILENT);
    }

    @Override
    public void setSilent(boolean silent) {
        metadata.set(MetadataIndex.SILENT, silent);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        bukkitMetadata.setMetadata(this, metadataKey, newMetadataValue);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Metadata

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

    @Override
    public boolean isPermissionSet(String s) {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Permissions

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean b) {

    }

    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        // Override in subclasses to implement behavior
        return false;
    }

    private Spigot spigot = new Spigot() {
        @Override
        public boolean isInvulnerable() {
            return GlowEntity.this.isInvulnerable();
        }
    };

    public Spigot spigot() {
        return spigot;
    }

    @Override
    public int hashCode() {
        int prime = 31;
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
        return id == other.id;
    }

    /**
     * The metadata store class for entities.
     */
    private static class EntityMetadataStore extends MetadataStoreBase<Entity> implements MetadataStore<Entity> {
        @Override
        protected String disambiguate(Entity subject, String metadataKey) {
            return subject.getUniqueId() + ":" + metadataKey;
        }
    }
}
