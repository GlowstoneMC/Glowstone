package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.objects.OreVein;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.material.Stone;
import org.bukkit.material.types.StoneType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * Populates the world with ores. To get the complete set, we must also use
 * {@link net.glowstone.generator.decorators.overworld.EmeraldOreDecorator}.
 */
public class OrePopulator extends BlockPopulator {

    private final Map<OreType, Integer> ores = new LinkedHashMap<>();

    /**
     * Creates a populator for dirt, gravel, andesite, diorite, granite; and coal, iron, gold,
     * redstone, diamond and lapis lazuli ores.
     */
    public OrePopulator() {
        ores.put(new OreType(Material.DIRT, 0, 256, 32), 10);
        ores.put(new OreType(Material.GRAVEL, 0, 256, 32), 8);
        ores.put(new OreType(Material.STONE, new Stone(StoneType.GRANITE), 0, 80, 32), 10);
        ores.put(new OreType(Material.STONE, new Stone(StoneType.DIORITE), 0, 80, 32), 10);
        ores.put(new OreType(Material.STONE, new Stone(StoneType.ANDESITE), 0, 80, 32), 10);
        ores.put(new OreType(Material.COAL_ORE, 0, 128, 16), 20);
        ores.put(new OreType(Material.IRON_ORE, 0, 64, 8), 20);
        ores.put(new OreType(Material.GOLD_ORE, 0, 32, 8), 2);
        ores.put(new OreType(Material.REDSTONE_ORE, 0, 16, 7), 8);
        ores.put(new OreType(Material.DIAMOND_ORE, 0, 16, 7), 1);
        ores.put(new OreType(Material.LAPIS_ORE, 16, 16, 6), 1);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int cx = chunk.getX() << 4;
        int cz = chunk.getZ() << 4;

        for (Entry<OreType, Integer> entry : ores.entrySet()) {

            OreType oreType = entry.getKey();
            for (int n = 0; n < entry.getValue(); n++) {

                int sourceX = cx + random.nextInt(16);
                int sourceZ = cz + random.nextInt(16);
                int sourceY = oreType.getRandomHeight(random);

                new OreVein(oreType).generate(world, random, sourceX, sourceY, sourceZ);
            }
        }
    }

}
