package net.glowstone.world;

import java.util.Random;

import net.glowstone.GlowChunk;
import net.glowstone.GlowWorld;

import org.bukkit.Material;

/**
 * A {@link WorldGenerator} that generates chunks with trees randomly placed.
 * @author Zhuowei Zhang
 */
public final class ForestWorldGenerator extends FlatGrassWorldGenerator {

	private static final int MAX_TREES = 2;

	private static final int TREE_MIN_HEIGHT = 6;

	private static final int TREE_MAX_HEIGHT = 9;

	private static final int TREE_CANOPY_HEIGHT = 5;

	private static final int TREE_CANOPY_WIDTH = 5;

	private static final int TREE_TYPE_NORMAL = 0;

	private static final int TREE_TYPE_REDWOOD = 1;

	private static final int TREE_TYPE_BIRCH = 2;

	private Random random = new Random();

	@Override
	public GlowChunk generate(GlowWorld world, int chunkX, int chunkZ) {
		GlowChunk chunk = super.generate(world, chunkX, chunkZ);

		int numTrees = random.nextInt(MAX_TREES + 1);
		for (int i = 0; i < numTrees; i++) {
			int x = random.nextInt(GlowChunk.WIDTH - (TREE_CANOPY_WIDTH * 2) + TREE_CANOPY_WIDTH);
			int z = random.nextInt(GlowChunk.HEIGHT- (TREE_CANOPY_WIDTH * 2) + TREE_CANOPY_WIDTH);
			int height = random.nextInt(TREE_MAX_HEIGHT - TREE_MIN_HEIGHT) + TREE_MIN_HEIGHT;
			int type = random.nextInt(3); // standard, redwood, birch
			makeTree(chunk, x, z, 61, height, type);
		}

		return chunk;
	}

	/** Grows a tree in a chunk. */
	private static void makeTree(GlowChunk chunk, int x, int z, int y, int height, int type){
		if (type != TREE_TYPE_NORMAL && type != TREE_TYPE_BIRCH && type != TREE_TYPE_REDWOOD) {
			throw new IllegalArgumentException("Type of tree not valid");
		}

		int center = (TREE_CANOPY_WIDTH) / 2;
		int trunkX = x + center;
		int trunkZ = z + center;


		for (int i = 0; i < height - TREE_CANOPY_HEIGHT; i++) {  // Generate the trunk
			chunk.setType(trunkX, trunkZ, y + i, Material.LOG.getId());
			chunk.setMetaData(trunkX, trunkZ, y + i, type);
		}

		for (int cy = height - TREE_CANOPY_HEIGHT; cy < height; cy++) { // Generate leaves

			int startX = x;
			int endX = x + TREE_CANOPY_WIDTH;

			int startZ = z;
			int endZ = z + TREE_CANOPY_HEIGHT;

			// make the canopy smaller at the top or bottom
			if (cy == height - TREE_CANOPY_HEIGHT || cy == height - 1) {
				startX++;
				endX--;

				startZ++;
				endZ--;
			}

			for (int cx = startX; cx < endX; cx++) {
				for (int cz = startZ; cz < endZ; cz++) {
					if (cx == trunkX && cz == trunkZ && cy < (height - 2)) { // trunk, leave some leaves above it
						chunk.setType(trunkX, trunkZ, y + cy, Material.LOG.getId());
						chunk.setMetaData(trunkX, trunkZ, y + cy, type);
					} else {
						chunk.setType(cx, cz, y + cy, Material.LEAVES.getId());
						chunk.setMetaData(cx, cz, y + cy, type);
					}
				}
			}
		}
	}

}

