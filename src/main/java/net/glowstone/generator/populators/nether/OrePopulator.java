package net.glowstone.generator.populators.nether;

import net.glowstone.generator.objects.OreType;
import net.glowstone.generator.populators.common.BaseOrePopulator;
import org.bukkit.Material;

/**
 * Populates the world with ores.
 */
public class OrePopulator extends BaseOrePopulator {
    public OrePopulator() {
        ores.put(new OreType(Material.NETHER_QUARTZ_ORE, 10, 118, 13, Material.NETHERRACK), 16);
        ores.put(new OreType(Material.MAGMA_BLOCK, 26, 37, 32, Material.NETHERRACK), 16);
    }
}
