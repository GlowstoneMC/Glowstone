package net.glowstone.generator.biomegrid;

import net.glowstone.constants.GlowBiome;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.bukkit.block.Biome.BADLANDS;
import static org.bukkit.block.Biome.BADLANDS_PLATEAU;
import static org.bukkit.block.Biome.BEACH;
import static org.bukkit.block.Biome.DEEP_OCEAN;
import static org.bukkit.block.Biome.ERODED_BADLANDS;
import static org.bukkit.block.Biome.GRAVELLY_MOUNTAINS;
import static org.bukkit.block.Biome.ICE_SPIKES;
import static org.bukkit.block.Biome.MODIFIED_BADLANDS_PLATEAU;
import static org.bukkit.block.Biome.MODIFIED_GRAVELLY_MOUNTAINS;
import static org.bukkit.block.Biome.MODIFIED_WOODED_BADLANDS_PLATEAU;
import static org.bukkit.block.Biome.MOUNTAINS;
import static org.bukkit.block.Biome.MUSHROOM_FIELDS;
import static org.bukkit.block.Biome.MUSHROOM_FIELD_SHORE;
import static org.bukkit.block.Biome.OCEAN;
import static org.bukkit.block.Biome.SNOWY_BEACH;
import static org.bukkit.block.Biome.SNOWY_MOUNTAINS;
import static org.bukkit.block.Biome.SNOWY_TAIGA;
import static org.bukkit.block.Biome.SNOWY_TAIGA_HILLS;
import static org.bukkit.block.Biome.SNOWY_TAIGA_MOUNTAINS;
import static org.bukkit.block.Biome.SNOWY_TUNDRA;
import static org.bukkit.block.Biome.STONE_SHORE;
import static org.bukkit.block.Biome.SWAMP;
import static org.bukkit.block.Biome.WOODED_BADLANDS_PLATEAU;
import static org.bukkit.block.Biome.WOODED_MOUNTAINS;

public class ShoreMapLayer extends MapLayer {

    private static final Set<Integer> OCEANS = new HashSet<>();
    private static final Map<Integer, Integer> SPECIAL_SHORES = new HashMap<>();

    static {
        OCEANS.add(GlowBiome.getId(OCEAN));
        OCEANS.add(GlowBiome.getId(DEEP_OCEAN));

        SPECIAL_SHORES.put(GlowBiome.getId(MOUNTAINS), GlowBiome.getId(STONE_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(WOODED_MOUNTAINS), GlowBiome.getId(STONE_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(GRAVELLY_MOUNTAINS), GlowBiome.getId(STONE_SHORE));
        SPECIAL_SHORES
            .put(GlowBiome.getId(MODIFIED_GRAVELLY_MOUNTAINS), GlowBiome.getId(STONE_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_TUNDRA), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_MOUNTAINS), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(ICE_SPIKES), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_TAIGA), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_TAIGA_HILLS), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES.put(GlowBiome.getId(SNOWY_TAIGA_MOUNTAINS), GlowBiome.getId(SNOWY_BEACH));
        SPECIAL_SHORES
            .put(GlowBiome.getId(MUSHROOM_FIELDS), GlowBiome.getId(MUSHROOM_FIELD_SHORE));
        SPECIAL_SHORES.put(GlowBiome.getId(SWAMP), GlowBiome.getId(SWAMP));
        SPECIAL_SHORES.put(GlowBiome.getId(BADLANDS), GlowBiome.getId(BADLANDS));
        SPECIAL_SHORES.put(GlowBiome.getId(WOODED_BADLANDS_PLATEAU), GlowBiome.getId(WOODED_BADLANDS_PLATEAU));
        SPECIAL_SHORES.put(GlowBiome.getId(MODIFIED_WOODED_BADLANDS_PLATEAU),
                GlowBiome.getId(MODIFIED_WOODED_BADLANDS_PLATEAU));
        SPECIAL_SHORES.put(GlowBiome.getId(BADLANDS_PLATEAU), GlowBiome.getId(BADLANDS_PLATEAU));
        SPECIAL_SHORES.put(GlowBiome.getId(MODIFIED_BADLANDS_PLATEAU),
            GlowBiome.getId(MODIFIED_BADLANDS_PLATEAU));
        SPECIAL_SHORES.put(GlowBiome.getId(ERODED_BADLANDS), GlowBiome.getId(ERODED_BADLANDS));
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
                                    : GlowBiome.getId(BEACH);
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }
}
