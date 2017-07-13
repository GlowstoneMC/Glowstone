package net.glowstone.io.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * The base for entity store classes.
 *
 * @param <T> The type of entity being stored.
 */
public abstract class EntityStore<T extends GlowEntity> {
    protected final Class<? extends T> clazz;
    protected final String type;

    public EntityStore(Class<? extends T> clazz, EntityType type) {
        this.clazz = clazz;
        this.type = type.getName();
    }
    
    public EntityStore(Class<? extends T> clazz, String name) {
        this.type = name;
        this.clazz = clazz;
    }

    public final String getEntityType() {
        return type;
    }

    public final Class<? extends T> getType() {
        return clazz;
    }

    /**
     * Create a new entity of this store's type at the given location. The
     * load method will be called separately.
     *
     * @param location The location.
     * @param compound The entity's tag, if extra data is needed.
     * @return The new entity.
     */
    public abstract T createEntity(Location location, CompoundTag compound);

    // For information on the NBT tags loaded here and elsewhere:
    // http://minecraft.gamepedia.com/Chunk_format#Entity_Format

    // todo: the following tags
    // - bool "Invulnerable"
    // - int "PortalCooldown"

    /**
     * Load data into an existing entity of the appropriate type from the
     * given compound tag.
     *
     * @param entity The target entity.
     * @param tag    The entity's tag.
     */
    public void load(T entity, CompoundTag tag) {
        // id, world, and location are handled by EntityStore
        // base stuff for all entities is here:

        if (tag.isList("Motion", TagType.DOUBLE)) {
            entity.setVelocity(NbtSerialization.listToVector(tag.getList("Motion", TagType.DOUBLE)));
        }
        if (tag.isFloat("FallDistance")) {
            entity.setFallDistance(tag.getFloat("FallDistance"));
        }
        if (tag.isShort("Fire")) {
            entity.setFireTicks(tag.getShort("Fire"));
        }
        if (tag.isByte("OnGround")) {
            entity.setOnGround(tag.getBool("OnGround"));
        }
        if (tag.isByte("NoGravity")) {
            entity.setGravity(!tag.getBool("NoGravity"));
        }
        if (tag.isByte("Silent")) {
            entity.setSilent(tag.getBool("Silent"));
        }
        if (tag.isByte("Glowing")) {
            entity.setGlowing(tag.getBool("Glowing"));
        }
        if (tag.isByte("Invulnerable")) {
            entity.setInvulnerable(tag.getBool("Invulnerable"));
        }

        if (tag.isLong("UUIDMost") && tag.isLong("UUIDLeast")) {
            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            entity.setUniqueId(uuid);
        } else if (tag.isString("UUID")) {
            // deprecated string format
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            entity.setUniqueId(uuid);
        }

        if (tag.isList("Passengers", TagType.COMPOUND)) {
            for (CompoundTag entityTag : tag.getCompoundList("Passengers")) {
                Entity passenger = loadPassenger(entity, entityTag);
                if (passenger != null) {
                    entity.addPassenger(passenger);
                }
            }
        }
    }

    private Entity loadPassenger(T vehicle, CompoundTag compoundTag) {
        Location location = NbtSerialization.listTagsToLocation(vehicle.getWorld(), compoundTag);

        if (location == null) {
            // We need a location to spawn the entity.
            // since there is no position in the entities nbt,
            // just spawn the passenger at the vehicle.
            // Later on, Entity.addPassenger will make sure of the teleportation
            // to the right coordinates.
            NbtSerialization.locationToListTags(vehicle.getLocation(), compoundTag);
        }

        try {
            // note that creating the entity is sufficient to add it to the world
            return EntityStorage.loadEntity(vehicle.getWorld(), compoundTag);
        } catch (Exception e) {
            String id = compoundTag.isString("id") ? compoundTag.getString("id") : "<missing>";
            if (e.getMessage() != null && e.getMessage().startsWith("Unknown entity type to load:")) {
                GlowServer.logger.warning("Skipping Entity with id " + id);
            } else {
                GlowServer.logger.log(Level.WARNING, "Error loading entity " + id, e);
            }
        }
        return null;
    }

    /**
     * Save information about this entity to the given tag.
     *
     * @param entity The entity to save.
     * @param tag    The target tag.
     */
    public void save(T entity, CompoundTag tag) {
        tag.putString("id", "minecraft:" + type);

        // write world info, Pos, Rotation, and Motion
        Location loc = entity.getLocation();
        NbtSerialization.writeWorld(loc.getWorld(), tag);
        NbtSerialization.locationToListTags(loc, tag);
        tag.putList("Motion", TagType.DOUBLE, NbtSerialization.vectorToList(entity.getVelocity()));

        tag.putFloat("FallDistance", entity.getFallDistance());
        tag.putShort("Fire", entity.getFireTicks());
        tag.putBool("OnGround", entity.isOnGround());

        tag.putLong("UUIDMost", entity.getUniqueId().getMostSignificantBits());
        tag.putLong("UUIDLeast", entity.getUniqueId().getLeastSignificantBits());

        tag.putBool("NoGravity", !entity.hasGravity());
        tag.putBool("Silent", entity.isSilent());
        tag.putBool("Invulnerable", entity.isInvulnerable());
        tag.putBool("Glowing", entity.isGlowing());

        // in case Vanilla or CraftBukkit expects non-living entities to have this tag
        tag.putInt("Air", 300);
        savePassengers(entity, tag);
    }

    private void savePassengers(GlowEntity vehicle, CompoundTag tag) {
        List<CompoundTag> passengers = new ArrayList<>();
        for (Entity passenger : vehicle.getPassengers()) {
            if (!(passenger instanceof GlowEntity)) {
                continue;
            }
            GlowEntity glowEntity = (GlowEntity) passenger;
            if (!glowEntity.shouldSave()) {
                continue;
            }
            try {
                CompoundTag compound = new CompoundTag();
                EntityStorage.save(glowEntity, compound);
                passengers.add(compound);
                savePassengers(glowEntity, compound);
            } catch (Exception e) {
                GlowServer.logger.log(Level.WARNING, "Error saving " + passenger + " from vehicle " + vehicle, e);
            }
        }
        if (!passengers.isEmpty()) {
            tag.putCompoundList("Passengers", passengers);
        }
    }
}
