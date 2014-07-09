package net.glowstone.io.entity;

import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * The class responsible for mapping entity types to their storage methods
 * and reading and writing entity data using those storage methods.
 */
public final class EntityStorage {

    private EntityStorage() {
    }

    /**
     * A table which maps entity ids to compound readers. This is generally used to map
     * stored entities to actual entities.
     */
    private static final Map<String, EntityStore<?>> idTable = new HashMap<>();

    /**
     * A table which maps entities to stores. This is generally used to map
     * entities being stored.
     */
    private static final Map<Class<? extends GlowEntity>, EntityStore<?>> classTable = new HashMap<>();

    /**
     * Populates the maps with stores.
     */
    static {
        bind(new PlayerStore());
        bind(new ItemStore());
    }

    /**
     * Binds a store by adding entries for it to the tables.
     * @param store The store object.
     * @param <T> The type of entity.
     */
    private static <T extends GlowEntity> void bind(EntityStore<T> store) {
        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Load a new entity in the given world from the given data tag.
     * @param world The target world.
     * @param compound The tag to load from.
     * @return The newly constructed entity.
     * @throws IllegalArgumentException if there is an error in the data.
     */
    public static GlowEntity loadEntity(GlowWorld world, CompoundTag compound) {
        // look up the store by the tag's id
        if (!compound.isString("id")) {
            throw new IllegalArgumentException("Entity has no type");
        }
        EntityStore<?> store = idTable.get(compound.getString("id"));
        if (store == null) {
            throw new IllegalArgumentException("Unknown entity type to load: \"" + compound.getString("id") + "\"");
        }

        // verify that, if the tag contains a world, it's correct
        World checkWorld = NbtSerialization.readWorld(world.getServer(), compound);
        if (checkWorld != null && checkWorld != world) {
            throw new IllegalArgumentException("Entity in wrong world: stored in " + world + " but data says " + checkWorld);
        }

        // find out the entity's location
        Location location = NbtSerialization.listTagsToLocation(world, compound);
        if (location == null) {
            throw new IllegalArgumentException("Entity has no location");
        }

        // create the entity instance and read the rest of the data
        return createEntity(store, location, compound);
    }

    /**
     * Helper method to call EntityStore methods for type safety.
     */
    private static <T extends GlowEntity> T createEntity(EntityStore<T> store, Location location, CompoundTag compound) {
        T entity = store.createEntity(location, compound);
        store.load(entity, compound);
        return entity;
    }

    /**
     * Finds a store by entity class, throwing an exception if not found.
     */
    private static EntityStore<?> find(Class<? extends GlowEntity> clazz, String type) {
        EntityStore<?> store = classTable.get(clazz);
        if (store == null) {
            // todo: maybe try to look up a parent class's store if one isn't found
            throw new IllegalArgumentException("Unknown entity type to " + type + ": " + clazz);
        }
        return store;
    }

    /**
     * Unsafe-cast an unknown EntityStore to the base type.
     */
    @SuppressWarnings("unchecked")
    private static EntityStore<GlowEntity> getBaseStore(EntityStore<?> store) {
        return ((EntityStore<GlowEntity>) store);
    }

    /**
     * Save an entity's data to the given compound tag.
     * @param entity The entity to save.
     * @param compound The target tag.
     */
    public static void save(GlowEntity entity, CompoundTag compound) {
        // look up the store for the entity
        EntityStore<?> store = find(entity.getClass(), "save");

        // EntityStore knows how to save world and location information
        getBaseStore(store).save(entity, compound);
    }

    /**
     * Load an entity's data from the given compound tag.
     * @param entity The target entity.
     * @param compound The tag to load from.
     */
    public static void load(GlowEntity entity, CompoundTag compound) {
        // look up the store for the entity
        EntityStore<?> store = find(entity.getClass(), "load");

        // work out the entity's location, using its current location if unavailable
        World world = NbtSerialization.readWorld(entity.getServer(), compound);
        if (world == null) {
            world = entity.getWorld();
        }
        Location location = NbtSerialization.listTagsToLocation(world, compound);
        if (location != null) {
            entity.teleport(location);
        }

        // read the rest of the entity's information
        getBaseStore(store).load(entity, compound);
    }

}
