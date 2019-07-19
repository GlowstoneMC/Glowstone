package net.glowstone.constants;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.glowstone.GlowServer.getWorldConfig;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_BIG_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_BIG_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEEP_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEFAULT;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_DEFAULT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_EXTREME_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLATLANDS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLATLANDS_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_FLAT_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HIGH_PLATEAU;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HIGH_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_LOW_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_LOW_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_MID_PLAINS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_RIVER;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_ROCKY_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_SWAMPLAND;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_HEIGHT_SWAMPLAND_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_BIG_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_BIG_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEEP_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEFAULT;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_DEFAULT_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_EXTREME_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLATLANDS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLATLANDS_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_FLAT_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HIGH_PLATEAU;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HIGH_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_LOW_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_LOW_SPIKES;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_HILLS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_HILLS2;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_MID_PLAINS;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_OCEAN;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_RIVER;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_ROCKY_SHORE;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_SWAMPLAND;
import static net.glowstone.util.config.WorldConfig.Key.BIOME_SCALE_SWAMPLAND_HILLS;
import static org.bukkit.block.Biome.ICE_SPIKES;
import static org.bukkit.block.Biome.SNOWY_TAIGA;
import static org.bukkit.block.Biome.SNOWY_TAIGA_MOUNTAINS;
import static org.bukkit.block.Biome.SNOWY_TUNDRA;
import static org.bukkit.block.Biome.values;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.lang.reflect.Constructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.generator.ground.GroundGenerator;
import net.glowstone.generator.ground.SnowyGroundGenerator;
import net.glowstone.generator.populators.overworld.BiomePopulator;
import net.glowstone.generator.populators.overworld.SnowyTundraPopulator;
import net.glowstone.generator.populators.overworld.IceSpikesPopulator;
import net.glowstone.generator.populators.overworld.TaigaPopulator;
import net.glowstone.i18n.ConsoleMessages;
import org.bukkit.block.Biome;

/**
 * Mappings for Biome id values.
 */
@Builder
@Data
public final class GlowBiome {

    private static final int[] ids = new int[values().length];
    private static final GlowBiome[] biomes = new GlowBiome[256];
    private static ClassToInstanceMap<BiomePopulator> populators = MutableClassToInstanceMap.create();
    private static ClassToInstanceMap<GroundGenerator> groundGenerators = MutableClassToInstanceMap.create();

    static {
        register(
                builder()
                        .type(SNOWY_TUNDRA)
                        .id(12)
                        .temperature(0.0)
                        .populator(SnowyTundraPopulator.class)
                        .ground(GroundGenerator.class)
                        .scale(BiomeScale.FLATLANDS)
                        .build(),
                builder()
                        .type(ICE_SPIKES)
                        .id(140)
                        .temperature(0.0)
                        .populator(IceSpikesPopulator.class)
                        .ground(SnowyGroundGenerator.class)
                        .scale(BiomeScale.MID_HILLS)
                        .build(),
                builder()
                        .type(SNOWY_TAIGA)
                        .id(30)
                        .temperature(-0.5)
                        .populator(TaigaPopulator.class)
                        .ground(GroundGenerator.class)
                        .scale(BiomeScale.MID_PLAINS)
                        .build(),
                builder()
                        .type(SNOWY_TAIGA_MOUNTAINS)
                        .id(158)
                        .temperature(-0.5)
                        .populator(TaigaPopulator.class)
                        .ground(GroundGenerator.class)
                        .scale(BiomeScale.MID_HILLS)
                        .build()
        );
        // Make caches immutable
        populators = ImmutableClassToInstanceMap.copyOf(populators);
        groundGenerators = ImmutableClassToInstanceMap.copyOf(groundGenerators);
    }

    private final int id;
    private final Biome type;
    private final double temperature;
    private final BiomePopulator populator;
    private final GroundGenerator ground;
    private final BiomeScale scale;

    /**
     * Get the biome ID for a specified Biome.
     *
     * @param biome the Biome.
     * @return the biome id, or -1
     */
    public static int getId(Biome biome) {
        checkNotNull(biome, "Biome cannot be null");
        return ids[biome.ordinal()];
    }

    /**
     * Get the GlowBiome for a specified id.
     *
     * @param id the id.
     * @return the Biome, or null
     */
    public static GlowBiome getBiome(int id) {
        if (id < biomes.length) {
            return biomes[id];
        } else {
            ConsoleMessages.Error.Biome.UNKNOWN.log(id);
            return null;
        }
    }

    /**
     * Get the GlowBiome for a specified biome type.
     *
     * @param biome the biome type.
     * @return the Biome, or null
     */
    public static GlowBiome getBiome(Biome biome) {
        return getBiome(getId(biome));
    }

    private static void register(GlowBiome... biomes) {
        for (GlowBiome biome : biomes) {
            GlowBiome.ids[biome.type.ordinal()] = biome.id;
            GlowBiome.biomes[biome.id] = biome;
        }
    }

    public static class GlowBiomeBuilder {
        public GlowBiomeBuilder populator(Class<? extends BiomePopulator> populatorClass, Object... args) {
            this.populator = cachedInstanceClass(
                    populators,
                    populatorClass,
                    args
            );
            return this;
        }

        public GlowBiomeBuilder ground(Class<? extends GroundGenerator> groundClass, Object... args) {
            this.ground = cachedInstanceClass(
                    groundGenerators,
                    groundClass,
                    args
            );
            return this;
        }
    }

    private static <T> T cachedInstanceClass(ClassToInstanceMap<T> cacheMap,
                                             Class<? extends T> clazz, Object... args) {
        if (args.length == 0) {
            // Cache
            if (cacheMap.containsKey(clazz)) {
                return cacheMap.getInstance(clazz);
            } else {
                try {
                    return cacheMap.put(clazz, clazz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Don't cache if args are required
            T instance = null;
            for (Constructor<?> constructor : clazz.getConstructors()) {
                // Try to call constructor, silently ignore exceptions
                try {
                    instance = (T) constructor.newInstance(args);
                    break;
                } catch (Exception ignored) {
                    // Ignored exception, go to next constructor
                }
            }
            if (instance == null) {
                throw new IllegalArgumentException("Could not find a constructor for given parameters.");
            }
            return instance;
        }
        return null;
    }

    @RequiredArgsConstructor
    private static class BiomeScale {
        public static final BiomeScale DEFAULT = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEFAULT),
                getWorldConfig().getDouble(BIOME_SCALE_DEFAULT));
        public static final BiomeScale FLAT_SHORE = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLAT_SHORE),
                getWorldConfig().getDouble(BIOME_SCALE_FLAT_SHORE));
        public static final BiomeScale HIGH_PLATEAU = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_HIGH_PLATEAU),
                getWorldConfig().getDouble(BIOME_SCALE_HIGH_PLATEAU));
        public static final BiomeScale FLATLANDS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLATLANDS),
                getWorldConfig().getDouble(BIOME_SCALE_FLATLANDS));
        public static final BiomeScale SWAMPLAND = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_SWAMPLAND),
                getWorldConfig().getDouble(BIOME_SCALE_SWAMPLAND));
        public static final BiomeScale MID_PLAINS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_PLAINS),
                getWorldConfig().getDouble(BIOME_SCALE_MID_PLAINS));
        public static final BiomeScale FLATLANDS_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_FLATLANDS_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_FLATLANDS_HILLS));
        public static final BiomeScale SWAMPLAND_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_SWAMPLAND_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_SWAMPLAND_HILLS));
        public static final BiomeScale LOW_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_LOW_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_LOW_HILLS));
        public static final BiomeScale HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_HILLS));
        public static final BiomeScale MID_HILLS2 = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_HILLS2),
                getWorldConfig().getDouble(BIOME_SCALE_MID_HILLS2));
        public static final BiomeScale DEFAULT_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEFAULT_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_DEFAULT_HILLS));
        public static final BiomeScale MID_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_MID_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_MID_HILLS));
        public static final BiomeScale BIG_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_BIG_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_BIG_HILLS));
        public static final BiomeScale BIG_HILLS2 = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_BIG_HILLS2),
                getWorldConfig().getDouble(BIOME_SCALE_BIG_HILLS2));
        public static final BiomeScale EXTREME_HILLS = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_EXTREME_HILLS),
                getWorldConfig().getDouble(BIOME_SCALE_EXTREME_HILLS));
        public static final BiomeScale ROCKY_SHORE = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_ROCKY_SHORE),
                getWorldConfig().getDouble(BIOME_SCALE_ROCKY_SHORE));
        public static final BiomeScale LOW_SPIKES = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_LOW_SPIKES),
                getWorldConfig().getDouble(BIOME_SCALE_LOW_SPIKES));
        public static final BiomeScale HIGH_SPIKES = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_HIGH_SPIKES),
                getWorldConfig().getDouble(BIOME_SCALE_HIGH_SPIKES));
        public static final BiomeScale RIVER = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_RIVER),
                getWorldConfig().getDouble(BIOME_SCALE_RIVER));
        public static final BiomeScale OCEAN = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_OCEAN),
                getWorldConfig().getDouble(BIOME_SCALE_OCEAN));
        public static final BiomeScale DEEP_OCEAN = new BiomeScale(
                getWorldConfig().getDouble(BIOME_HEIGHT_DEEP_OCEAN),
                getWorldConfig().getDouble(BIOME_SCALE_DEEP_OCEAN));

        @Getter
        private final double height;
        @Getter
        private final double scale;
    }
}
