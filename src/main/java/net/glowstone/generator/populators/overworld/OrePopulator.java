package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.populators.common.BaseOrePopulator;
import org.bukkit.Material;

/**
 * Populates the world with ores. To get the complete set, we must also use
 * {@link net.glowstone.generator.decorators.overworld.EmeraldOreDecorator}.
 */
public class OrePopulator extends BaseOrePopulator {
    /**
     * Creates a populator for dirt, gravel, andesite, diorite, granite; and coal, iron, gold,
     * redstone, diamond and lapis lazuli ores.
     */
    public OrePopulator() {
        ores.put(new OreType(Material.DIRT, 0, 256, 32), 10);
        ores.put(new OreType(Material.GRAVEL, 0, 256, 32), 8);
        ores.put(new OreType(Material.GRANITE, 0, 80, 32), 10);
        ores.put(new OreType(Material.DIORITE, 0, 80, 32), 10);
        ores.put(new OreType(Material.ANDESITE, 0, 80, 32), 10);
        ores.put(new OreType(Material.COAL_ORE, 0, 128, 16), 20);
        ores.put(new OreType(Material.IRON_ORE, 0, 64, 8), 20);
        ores.put(new OreType(Material.GOLD_ORE, 0, 32, 8), 2);
        ores.put(new OreType(Material.REDSTONE_ORE, 0, 16, 7), 8);
        ores.put(new OreType(Material.DIAMOND_ORE, 0, 16, 7), 1);
        ores.put(new OreType(Material.LAPIS_ORE, 16, 16, 6), 1);
    }
}
