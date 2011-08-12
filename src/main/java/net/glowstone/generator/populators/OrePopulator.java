package net.glowstone.generator.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

/**
 * Populates the world with ores.
 */
public class OrePopulator extends BlockPopulator {
    
	private static final int[] iterations = new int[] {10, 20, 20, 2, 8, 1, 1, 1};
	private static final int[] amount = new int[] {32, 16, 8, 8, 7, 7, 6};
	private static final int[] type = new int[] {Material.GRAVEL.getId(), Material.COAL_ORE.getId(),
		Material.IRON_ORE.getId(), Material.GOLD_ORE.getId(), Material.REDSTONE_ORE.getId(),
		Material.DIAMOND_ORE.getId(), Material.LAPIS_ORE.getId()};
	private static final int[] maxHeight = new int[] {128, 128, 128, 128, 128, 64,
		32, 16, 16, 32};
	private static final int STONE = Material.STONE.getId();

	@Override
	public void populate(World world, Random random, Chunk source) {
		for (int i = 0; i < type.length; i++) {
			for (int j = 0; j < iterations[i]; j++) {
				internal(source, random, random.nextInt(16),
						 random.nextInt(maxHeight[i]), random.nextInt(16),
						 amount[i], type[i]);
			}
		}
	}

	private static void internal(Chunk source, Random random, int originX,
								 int originY, int originZ, int amount, int type) {
		for (int i = 0; i < amount; i++) {
			int x = originX + random.nextInt(amount / 2) - amount / 4;
			int y = originY + random.nextInt(amount / 4) - amount / 8;
			int z = originZ + random.nextInt(amount / 2) - amount / 4;
			x &= 0xf;
			z &= 0xf;
			if (y > 127 || y < 0) {
				continue;
			}
			Block block = source.getBlock(x, y, z);
			if (block.getTypeId() == STONE) {
				block.setTypeId(type, false);
			}
		}
	}
}
