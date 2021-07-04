package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.EXTREME_HILLS_WITH_TREES;
import static org.bukkit.block.Biome.ICE_FLATS;
import static org.bukkit.block.Biome.JUNGLE;
import static org.bukkit.block.Biome.JUNGLE_EDGE;
import static org.bukkit.block.Biome.MESA;
import static org.bukkit.block.Biome.MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MESA_ROCK;
import static org.bukkit.block.Biome.PLAINS;
import static org.bukkit.block.Biome.REDWOOD_TAIGA;
import static org.bukkit.block.Biome.SWAMPLAND;
import static org.bukkit.block.Biome.TAIGA;
import static org.bukkit.block.Biome.TAIGA_COLD;

public class BiomeEdgeMapLayer extends MapLayer {

    private static final Map<Integer, Integer> MESA_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> MEGA_TAIGA_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> DESERT_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> SWAMP1_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> SWAMP2_EDGES = new HashMap<>();
    private static final Map<Map<Integer, Integer>, List<Integer>> EDGES = new HashMap<>();

    static {
        MESA_EDGES.put(GlowBiome.getId(MESA_ROCK), GlowBiome.getId(MESA));
        MESA_EDGES.put(GlowBiome.getId(MESA_CLEAR_ROCK), GlowBiome.getId(MESA));

        MEGA_TAIGA_EDGES.put(GlowBiome.getId(REDWOOD_TAIGA), GlowBiome.getId(TAIGA));

        DESERT_EDGES.put(GlowBiome.getId(DESERT), GlowBiome.getId(EXTREME_HILLS_WITH_TREES));

        SWAMP1_EDGES.put(GlowBiome.getId(SWAMPLAND), GlowBiome.getId(PLAINS));
        SWAMP2_EDGES.put(GlowBiome.getId(SWAMPLAND), GlowBiome.getId(JUNGLE_EDGE));

        EDGES.put(MESA_EDGES, null);
        EDGES.put(MEGA_TAIGA_EDGES, null);
        EDGES.put(DESERT_EDGES, Arrays.asList(GlowBiome.getId(ICE_FLATS)));
        EDGES.put(SWAMP1_EDGES, Arrays.asList(GlowBiome.getId(DESERT), GlowBiome.getId(TAIGA_COLD),
            GlowBiome.getId(ICE_FLATS)));
        EDGES.put(SWAMP2_EDGES, Arrays.asList(GlowBiome.getId(JUNGLE)));
    }

    private final MapLayer belowLayer;

    public BiomeEdgeMapLayer(long seed, MapLayer belowLayer) {
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
                // This applies biome large edges using Von Neumann neighborhood
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                int val = centerVal;
                for (Entry<Map<Integer, Integer>, List<Integer>> entry : EDGES.entrySet()) {
                    Map<Integer, Integer> map = entry.getKey();
                    if (map.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                        if (entry.getValue() == null && (
                                !map.containsKey(upperVal)
                                || !map.containsKey(lowerVal)
                                || !map.containsKey(leftVal)
                                || !map.containsKey(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        } else if (entry.getValue() != null && (
                                entry.getValue().contains(upperVal)
                                || entry.getValue().contains(lowerVal)
                                || entry.getValue().contains(leftVal)
                                || entry.getValue().contains(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        }
                    }
                }

                finalValues[j + i * sizeX] = val;
            }
        }
        return finalValues;
    }
}
