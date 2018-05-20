package net.glowstone.generator.objects;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

/**
 * A patch replaces specified blocks within a cylinder. It will delete flowers, tall grass and
 * mushrooms (but not crops, trees or desert vegetation) above it.
 */
public class BlockPatch implements TerrainObject {

    private static final int MIN_RADIUS = 2;
    private final Material type;
    private final int horizRadius;
    private final int vertRadius;
    private final List<Material> overridables;

    /**
     * Creates a patch.
     * @param type the ground cover block type
     * @param horizRadius the maximum radius on the horizontal plane
     * @param vertRadius the depth above and below the center
     * @param overridables the blocks that can be replaced
     */
    public BlockPatch(Material type, int horizRadius, int vertRadius, Material... overridables) {
        this.type = type;
        this.horizRadius = horizRadius;
        this.vertRadius = vertRadius;
        this.overridables = Arrays.asList(overridables);
    }

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean succeeded = false;
        int n = random.nextInt(horizRadius - MIN_RADIUS) + MIN_RADIUS;
        int nsquared = n * n;
        for (int x = sourceX - n; x <= sourceX + n; x++) {
            for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) > nsquared) {
                    continue;
                }
                for (int y = sourceY - vertRadius; y <= sourceY + vertRadius; y++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!overridables.contains(block.getType())) {
                        continue;
                    }
                    if (TerrainObject.killPlantAbove(block)) {
                        break;
                    }
                    BlockState state = block.getState();
                    state.setType(type);
                    state.setData(new MaterialData(type));
                    state.update(true);
                    succeeded = true;
                    break;
                }
            }
        }
        return succeeded;
    }
}
