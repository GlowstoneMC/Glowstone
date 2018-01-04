package net.glowstone.generator.biomegrid;

import static org.bukkit.block.Biome.BEACHES;
import static org.bukkit.block.Biome.COLD_BEACH;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.EXTREME_HILLS;
import static org.bukkit.block.Biome.EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.ICE_FLATS;
import static org.bukkit.block.Biome.ICE_MOUNTAINS;
import static org.bukkit.block.Biome.MESA;
import static org.bukkit.block.Biome.MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MESA_ROCK;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND;
import static org.bukkit.block.Biome.MUSHROOM_ISLAND_SHORE;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS;
import static org.bukkit.block.Biome.MUTATED_EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.MUTATED_ICE_FLATS;
import static org.bukkit.block.Biome.MUTATED_MESA;
import static org.bukkit.block.Biome.MUTATED_MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MUTATED_MESA_ROCK;
import static org.bukkit.block.Biome.MUTATED_TAIGA_COLD;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.STONE_BEACH;
import static org.bukkit.block.Biome.SWAMPLAND;
import static org.bukkit.block.Biome.TAIGA_COLD;
import static org.bukkit.block.Biome.TAIGA_COLD_HILLS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.glowstone.constants.GlowBiome;

public class ShoreMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> SPECIAL_SHORES = new HashMap<>();

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(EXTREME_HILLS_WITH_TREES), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_EXTREME_HILLS), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES
            .put(GlowBiome.getId(MUTATED_EXTREME_HILLS_WITH_TREES), GlowBiome.getId(STONE_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_FLATS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_MOUNTAINS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_ICE_FLATS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(TAIGA_COLD), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(TAIGA_COLD_HILLS), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_TAIGA_COLD), GlowBiome.getId(COLD_BEACH));
        SPECIAL_SHORES
            .put(GlowBiome.getId(MUSHROOM_ISLAND), GlowBiome.getId(MUSHROOM_ISLAND_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(SWAMPLAND), GlowBiome.getId(SWAMPLAND));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA), GlowBiome.getId(MESA));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_ROCK), GlowBiome.getId(MESA_ROCK));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_MESA_ROCK), GlowBiome.getId(MUTATED_MESA_ROCK));
        SPECIAL_SHORES.put(GlowBiome.getId(MESA_CLEAR_ROCK), GlowBiome.getId(MESA_CLEAR_ROCK));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_MESA_CLEAR_ROCK),
            GlowBiome.getId(MUTATED_MESA_CLEAR_ROCK));
        SPECIAL_SHORES.put(GlowBiome.getId(MUTATED_MESA), GlowBiome.getId(MUTATED_MESA));
    }

    private final MapLayer belowLayer;

    public ShoreMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                // This applies shores using Von Neumann neighborhood
                // it takes a 3x3 grid with a cross shape and analyzes values as follow
                // 0X0
                // XxX
                // 0X0
                // the grid center value decides how we are proceeding:
                // - if it's not ocean and it's surrounded by at least 1 ocean cell
                // it turns the center value into beach.
                int upperVal = values[j + 1 + i * gridSizeX];
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                int leftVal = values[j + (i + 1) * gridSizeX];
                int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (!OCEANS.contains(centerVal) && (
                        OCEANS.contains(upperVal) || OCEANS.contains(lowerVal)
                        || OCEANS.contains(leftVal) || OCEANS.contains(rightVal))) {
                    finalValues[j + i * sizeX] =
                            SPECIAL_SHORES.containsKey(centerVal)
                                    ? SPECIAL_SHORES.get(centerVal)
                                    : GlowBiome.getId(BEACHES);
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }
}
