package net.glowstone.io.structure;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.glowstone.GlowWorld;
import net.glowstone.generator.structures.GlowStructure;
import net.glowstone.util.nbt.CompoundTag;

/**
 * The class responsible for mapping structure types to their storage methods and reading and
 * writing structure data using those storage methods.
 */
public final class StructureStorage {

    /**
     * A table which maps structure ids to compound readers. This is generally used to map stored
     * structures to actual structures.
     */
    private static final Map<String, StructureStore<?>> idTable = new HashMap<>();
    /**
     * A table which maps structures to stores. This is generally used to map structures being
     * stored.
     */
    private static final Map<Class<? extends GlowStructure>, StructureStore<?>> classTable
            = new HashMap<>();

    /*
     * Populates the maps with stores.
     */
    static {
        bind(new TempleStore());
        //bind(new VillageStore());
        //bind(new StrongholdStore());
        //bind(new MineshaftStore());
    }

    private StructureStorage() {
    }

    /**
     * Binds a store by adding entries for it to the tables.
     *
     * @param store The store object.
     * @param <T> The type of structure.
     */
    private static <T extends GlowStructure> void bind(StructureStore<T> store) {
        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Returns all known structure stores.
     *
     * @return A collection containing all structure stores.
     */
    public static Collection<StructureStore<?>> getStructureStores() {
        return Collections.unmodifiableCollection(idTable.values());
    }

    /**
     * Load a structure in the given world from the given data tag.
     *
     * @param world The target world.
     * @param compound The tag to load from.
     * @return The newly constructed structure.
     * @throws IllegalArgumentException if there is an error in the data.
     */
    public static GlowStructure loadStructure(GlowWorld world, CompoundTag compound) {
        // look up the store by the tag's id
        if (!compound.isString("id")) {
            throw new IllegalArgumentException("Structure has no type");
        }
        StructureStore<?> store = idTable.get(compound.getString("id"));
        if (store == null) {
            throw new IllegalArgumentException(
                    "Unknown structure type: \"" + compound.getString("id") + "\"");
        }

        int x = 0;
        int z = 0;
        if (compound.isInt("ChunkX")) {
            x = compound.getInt("ChunkX");
        }
        if (compound.isInt("ChunkZ")) {
            z = compound.getInt("ChunkZ");
        }

        return createStructure(world, x, z, store, compound);
    }

    /**
     * Save a structure data to the given compound tag.
     *
     * @param structure The structure to save.
     * @param compound The target tag.
     * @return The structure store for the saved structure.
     */
    public static StructureStore<GlowStructure> saveStructure(GlowStructure structure,
            CompoundTag compound) {
        // look up the store for the structure
        StructureStore<?> store = classTable.get(structure.getClass());
        if (store == null) {
            throw new IllegalArgumentException(
                    "Unknown structure type to save: \"" + structure.getClass() + "\"");
        }

        compound.putString("id", store.getId());
        compound.putInt("ChunkX", structure.getChunkX());
        compound.putInt("ChunkZ", structure.getChunkZ());

        StructureStore<GlowStructure> baseStore = getBaseStore(store);
        baseStore.save(structure, compound);

        return baseStore;
    }

    /**
     * Helper method to call StructureStore methods for type safety.
     */
    private static <T extends GlowStructure> T createStructure(GlowWorld world, int chunkX,
            int chunkZ, StructureStore<T> store, CompoundTag compound) {
        T structure = store.createStructure(world, chunkX, chunkZ);
        store.load(structure, compound);
        return structure;
    }

    /**
     * Unsafe-cast an unknown StructureStore to the base type.
     */
    @SuppressWarnings("unchecked")
    private static StructureStore<GlowStructure> getBaseStore(StructureStore<?> store) {
        return (StructureStore<GlowStructure>) store;
    }
}
