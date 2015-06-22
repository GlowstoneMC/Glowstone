package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.*;
import java.util.Map.Entry;

import static org.bukkit.block.Biome.*;

public class BiomeThinEdgeMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> MESA_EDGES = new HashMap<>();
    private static final Map<Integer, Integer> JUNGLE_EDGES = new HashMap<>();
    private static final Map<Map<Integer, Integer>, List<Integer>> EDGES = new HashMap<>();

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
                    final Map<Integer, Integer> map = entry.getKey();
                    if (map.containsKey(centerVal)) {
                        int upperVal = values[j + 1 + i * gridSizeX];
                        int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                        int leftVal = values[j + (i + 1) * gridSizeX];
                        int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                        if (entry.getValue() == null && ((!OCEANS.contains(upperVal) && !map.containsKey(upperVal)) ||
                                (!OCEANS.contains(lowerVal) && !map.containsKey(lowerVal)) ||
                                (!OCEANS.contains(leftVal) && !map.containsKey(leftVal)) ||
                                (!OCEANS.contains(rightVal) && !map.containsKey(rightVal)))) {
                            val = map.get(centerVal);
                            break;
                        } else if (entry.getValue() != null && ((!OCEANS.contains(upperVal) && !entry.getValue().contains(upperVal)) ||
                                (!OCEANS.contains(lowerVal) && !entry.getValue().contains(lowerVal)) ||
                                (!OCEANS.contains(leftVal) && !entry.getValue().contains(leftVal)) ||
                                (!OCEANS.contains(rightVal) && !entry.getValue().contains(rightVal)))) {
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

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        MESA_EDGES.put(GlowBiome.getId(MESA), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_BRYCE), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_PLATEAU_FOREST), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_PLATEAU_FOREST_MOUNTAINS), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_PLATEAU), GlowBiome.getId(DESERT));
        MESA_EDGES.put(GlowBiome.getId(MESA_PLATEAU_MOUNTAINS), GlowBiome.getId(DESERT));

        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE_HILLS), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE_MOUNTAINS), GlowBiome.getId(JUNGLE_EDGE));
        JUNGLE_EDGES.put(GlowBiome.getId(JUNGLE_EDGE_MOUNTAINS), GlowBiome.getId(JUNGLE_EDGE));

        EDGES.put(MESA_EDGES, null);
        EDGES.put(JUNGLE_EDGES, Arrays.asList(GlowBiome.getId(JUNGLE), GlowBiome.getId(JUNGLE_HILLS),
                GlowBiome.getId(JUNGLE_MOUNTAINS), GlowBiome.getId(JUNGLE_EDGE_MOUNTAINS),
                GlowBiome.getId(FOREST), GlowBiome.getId(TAIGA)));
    }
}
