package net.glowstone.block.data;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import io.netty.util.collection.IntObjectHashMap;
import java.util.Map;
import net.glowstone.block.data.loaders.BlockDataLoader;
import net.glowstone.block.data.loaders.DefaultBlockDataLoader;
import net.glowstone.block.data.loaders.WaterloggedBlockDataLoader;
import org.bukkit.Material;

public class BlockDataStore {
    private static final ImmutableBiMap<Class<? extends BlockData>, BlockDataLoader>
            LOADERS = ImmutableBiMap.<Class<? extends BlockData>, BlockDataLoader>builder()
            .put(BlockData.class, new DefaultBlockDataLoader())
            .put(Waterlogged.class, new WaterloggedBlockDataLoader())
            .build();

    private static final IntObjectHashMap<SingletonBlockData> SINGLETONS_BY_HASHCODE
            = new IntObjectHashMap<>(10000);

    private static final IntObjectHashMap<SingletonBlockData> SINGLETONS_BY_PALETTE
            = new IntObjectHashMap<>(10000);

    static {
        // TODO: Do this from a JSON file
        load(Material.SIGN, 0, ImmutableMap.<String, String>builder()
                .put("waterlogged", "false")
                .build());
        load(Material.SIGN, 1, ImmutableMap.<String, String>builder()
                .put("waterlogged", "true")
                .build());
    }

    /**
     * Creates a SingletonBlockData instance from a Map format.
     *
     * @param material the Material in question
     * @param stateId  the global palette state ID
     * @param state    the block state in a string-string map
     * @return a SingletonBlockData instance matching the given data
     */
    public static SingletonBlockData load(
            Material material, int stateId, Map<String, String> state) {
        // TODO: Use material.getBlockDataClass
        Class<? extends BlockData> dataClass = Waterlogged.class;
        BlockDataLoader loader = findLoader(dataClass);

        SingletonBlockData singletonBlockData = loader.createSingletonBlockData(
                material, stateId, state);
        SINGLETONS_BY_HASHCODE.put(singletonBlockData.hashCode(), singletonBlockData);
        SINGLETONS_BY_PALETTE.put(singletonBlockData.getGlobalPaletteId(), singletonBlockData);

        return singletonBlockData;
    }

    /**
     * Gets the Singleton Block Data instance from the given mutable block data instance.
     *
     * @param data the mutable block data instance
     * @return the Singleton Block Data instance
     */
    @SuppressWarnings("unchecked")
    public static SingletonBlockData findSingleton(BlockData data) {
        BlockDataLoader loader = findLoader(data);
        int hashcode = loader.hashBlockData(data);

        return SINGLETONS_BY_HASHCODE.get(hashcode);
    }

    /**
     * Gets the global palette ID of the given block data.
     *
     * @param data the mutable block data
     * @return the global palette ID of the given block data
     */
    public static int getBlockDataPaletteId(BlockData data) {
        return findSingleton(data).getGlobalPaletteId();
    }

    /**
     * Gets the Singleton Block Data instance from a global palette ID.
     *
     * @param paletteId the global palette ID to lookup
     * @return the Singleton Block Data instance
     */
    public static SingletonBlockData getBlockDataById(int paletteId) {
        return SINGLETONS_BY_PALETTE.get(paletteId);
    }

    static BlockDataLoader findLoader(BlockData baseData) {
        return findLoader(baseData.getBaseClass());
    }

    static BlockDataLoader findLoader(Class<? extends BlockData> baseDataClass) {
        if (baseDataClass == null) {
            return LOADERS.get(BlockData.class);
        }
        return LOADERS.get(baseDataClass);
    }
}
