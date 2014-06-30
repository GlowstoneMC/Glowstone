package net.glowstone.io.entity;

import net.glowstone.entity.GlowEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;

import java.util.UUID;

/**
 * The base for entity store classes.
 * @param <T> The type of entity being stored.
 */
abstract class EntityStore<T extends GlowEntity> {
    private final String id;
    private final Class<T> clazz;

    public EntityStore(Class<T> clazz, String id) {
        this.id = id;
        this.clazz = clazz;
    }

    public final String getId() {
        return id;
    }

    public final Class<T> getType() {
        return clazz;
    }

    /**
     * Create a new entity of this store's type at the given location. The
     * load method will be called separately.
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
    // - compound "Riding"

    /**
     * Load data into an existing entity of the appropriate type from the
     * given compound tag.
     * @param entity The target entity.
     * @param tag The entity's tag.
     */
    public void load(T entity, CompoundTag tag) {
        // id, world, and location are handled by EntityStore
        // base stuff for all entities is here:

        if (tag.isList("Motion", TagType.DOUBLE)) {
            entity.setVelocity(NbtSerialization.listToVector(tag.<Double>getList("Motion", TagType.DOUBLE)));
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

        if (tag.isLong("UUIDMost") && tag.isLong("UUIDLeast")) {
            UUID uuid = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
            entity.setUniqueId(uuid);
        } else if (tag.isString("UUID")) {
            // deprecated string format
            UUID uuid = UUID.fromString(tag.getString("UUID"));
            entity.setUniqueId(uuid);
        }
    }

    /**
     * Save information about this entity to the given tag.
     * @param entity The entity to save.
     * @param tag The target tag.
     */
    public void save(T entity, CompoundTag tag) {
        tag.putString("id", id);

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

        // in case Vanilla or CraftBukkit expects non-living entities to have this tag
        tag.putInt("Air", 300);
    }
}
