package net.glowstone.generator.biomegrid;

import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.DESERT;
import static org.bukkit.block.Biome.FOREST;
import static org.bukkit.block.Biome.JUNGLE;
import static org.bukkit.block.Biome.JUNGLE_EDGE;
import static org.bukkit.block.Biome.JUNGLE_HILLS;
import static org.bukkit.block.Biome.MESA;
import static org.bukkit.block.Biome.MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MESA_ROCK;
import static org.bukkit.block.Biome.MUTATED_JUNGLE;
import static org.bukkit.block.Biome.MUTATED_JUNGLE_EDGE;
import static org.bukkit.block.Biome.MUTATED_MESA;
import static org.bukkit.block.Biome.MUTATED_MESA_CLEAR_ROCK;
import static org.bukkit.block.Biome.MUTATED_MESA_ROCK;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.TAIGA;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.glowstone.constants.GlowBiome;

public class BiomeThinEdgeMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> MESA_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> JUNGLE_EDGES = new HashMap<>();
    private static final Map<Map<Integer, Integer>, List<Integer>> EDGES = new HashMap<>();

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        MESA_EDGES.put(GlowBiome.getId(MESA), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MUTATED_MESA), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_ROCK), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MUTATED_MESA_ROCK), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_CLEAR_ROCK), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MUTATED_MESA_CLEAR_ROCK), GlowBiome.getId(DESERT));

        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE_HILLS), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(MUTATED_JUNGLE), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(MUTATED_JUNGLE_EDGE), GlowBiome.getId(JUNGLE_EDGE));

        EDGES.put(MESA_EDGES, null);
        EDGES
            .put(JUNGLE_EDGES, Arrays.asList(GlowBiome.getId(JUNGLE), GlowBiome.getId(JUNGLE_HILLS),
                GlowBiome.getId(MUTATED_JUNGLE), GlowBiome.getId(MUTATED_JUNGLE_EDGE),
                GlowBiome.getId(FOREST), GlowBiome.getId(TAIGA)));
    }

    private final MapLayer belowLayer;

    public BiomeThinEdgeMapLayer(long seed, MapLayer belowLayer) {
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
                // This applies biome thin edges using Von Neumann neighborhood
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                int val = centerVal;
                for (Entry<Map<Integer, Integer>, List<Integer>> entry : EDGES.entrySet()) {
                    Map<Integer, Integer> map = entry.getKey();
                    if (map.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                        List<Integer> entryValue = entry.getValue();
                        if (entryValue == null && (
                                !OCEANS.contains(upperVal) && !map.containsKey(upperVal)
                                || !OCEANS.contains(lowerVal) && !map.containsKey(lowerVal)
                                || !OCEANS.contains(leftVal) && !map.containsKey(leftVal)
                                || !OCEANS.contains(rightVal) && !map.containsKey(rightVal))) {
                            val = map.get(centerVal);
                            break;
                        } else if (entryValue != null && (
                                !OCEANS.contains(upperVal) && !entryValue.contains(upperVal)
                                || !OCEANS.contains(lowerVal) && !entryValue.contains(lowerVal)
                                || !OCEANS.contains(leftVal) && !entryValue.contains(leftVal)
                                || !OCEANS.contains(rightVal) && !entryValue.contains(rightVal))) {
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
