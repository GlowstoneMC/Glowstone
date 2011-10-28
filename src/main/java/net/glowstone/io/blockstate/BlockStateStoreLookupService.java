package net.glowstone.io.blockstate;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowEntity;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;

import java.util.HashMap;
import java.util.Map;

/**
 * A class used to lookup message codecs.
 * @author Graham Edgecombe
 */
public final class BlockStateStoreLookupService {

    /**
     * A table which maps entity ids to compound readers. This is generally used to map
     * stored entities to actual entities.
     */
    private static Map<String, BlockStateStore<?>> idTable = new HashMap<String, BlockStateStore<?>>();

    /**
     * A table which maps entities to stores. This is generally used to map
     * entities being stored.
     */
    private static Map<Class<? extends GlowBlockState>, BlockStateStore<?>> classTable = new HashMap<Class<? extends GlowBlockState>, BlockStateStore<?>>();

    /**
     * Populates the lookup maps with stores
     */
    static {
        try {
            bind(NoteBlockStore.class);
            bind(SignStore.class);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Binds a store by adding entries for it to the tables.
     * @param clazz The store's class.
     * @param <T> The type of store.
     * @param <C> The type of entity.
     * @throws InstantiationException if the store could not be instantiated.
     * @throws IllegalAccessException if the store could not be instantiated due
     * to an access violation.
     */
    private static <T extends GlowBlockState, C extends BlockStateStore<T>> void bind(Class<C> clazz) throws InstantiationException, IllegalAccessException {
        BlockStateStore<T> store = clazz.newInstance();

        idTable.put(store.getId(), store);
        classTable.put(store.getType(), store);
    }

    /**
     * Finds an entity store by entity id.
     * @param id The entity id.
     * @return The codec, or {@code null} if it could not be found.
     */
    public static BlockStateStore<?> find(String id) {
        return idTable.get(id);
    }

    /**
     * Finds a store by entity class.
     * @param clazz The entity class.
     * @param <T> The type of entity.
     * @return The store, or {@code null} if it could not be found.
     */
    @SuppressWarnings("unchecked")
    public static <T extends GlowBlockState> BlockStateStore<T> find(Class<T> clazz) {
        return (BlockStateStore<T>) classTable.get(clazz);
    }

    /**
     * Default private constructor to prevent instantiation.
     */
    private BlockStateStoreLookupService() {

    }

}
