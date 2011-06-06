package net.glowstone.generator;

import java.util.Random;
import org.bukkit.World;
import net.glowstone.GlowChunk;

/**
 * A {@link WorldGenerator} that generates chunks with trees randomly placed.
 * @author Zhuowei Zhang
 */
public final class FlatForestGenerator extends FlatgrassGenerator {

	private static final int MAX_TREES = 2;

	private static final int TREE_MIN_HEIGHT = 6;

	private static final int TREE_MAX_HEIGHT = 9;

	private static final int TREE_CANOPY_HEIGHT = 5;

	private static final int TREE_CANOPY_WIDTH = 5;

	@Override
	public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
		super.generate(world, random, chunkX, chunkZ);

		int numTrees = random.nextInt(MAX_TREES + 1);
		for (int i = 0; i < numTrees; i++) {
			int x = random.nextInt(GlowChunk.WIDTH - (TREE_CANOPY_WIDTH * 2) + TREE_CANOPY_WIDTH);
			int z = random.nextInt(GlowChunk.HEIGHT- (TREE_CANOPY_WIDTH * 2) + TREE_CANOPY_WIDTH);
			int height = random.nextInt(TREE_MAX_HEIGHT - TREE_MIN_HEIGHT) + TREE_MIN_HEIGHT;
			makeTree(x, z, 61, height);
		}

		return data;
	}

	/** Grows a tree in a chunk. */
	private void makeTree(int x, int z, int y, int height) {
		int center = (TREE_CANOPY_WIDTH) / 2;
		int trunkX = x + center;
		int trunkZ = z + center;

		for (int i = 0; i < height - TREE_CANOPY_HEIGHT; i++) {  // Generate the trunk
			set(trunkX, trunkZ, y + i, (byte) 17);
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
						set(trunkX, trunkZ, y + cy, (byte) 17);
					} else {
						set(cx, cz, y + cy, (byte) 18);
					}
				}
			}
		}
	}

}

