package net.glowstone.generator.objects;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DoublePlantSpecies;

/**
 * A patch replaces specified blocks within a cylinder. It will delete flowers, tall grass and
 * mushrooms (but not crops, trees or desert vegetation) above it.
 */
public class BlockPatch implements TerrainObject {

    private static final int MIN_RADIUS = 2;
    private static final List<Material> PLANT_TYPES = ImmutableList.of(
            Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE,
            Material.DOUBLE_PLANT, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM);
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
        for (int x = sourceX - n; x <= sourceX + n; x++) {
            for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= n * n) {
                    for (int y = sourceY - vertRadius; y <= sourceY + vertRadius; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (overridables.contains(block.getType())) {
                            Block blockAbove = block.getRelative(BlockFace.UP);
                            Material mat = blockAbove.getType();
                            if (PLANT_TYPES.contains(mat)) {
                                if (mat == Material.DOUBLE_PLANT) {
                                    MaterialData data = blockAbove.getState().getData();
                                    if (data instanceof DoublePlant && ((DoublePlant) data)
                                                .getSpecies() == DoublePlantSpecies.PLANT_APEX) {
                                        blockAbove.getRelative(BlockFace.UP)
                                            .setType(Material.AIR);
                                    }
                                }
                                blockAbove.setType(Material.AIR);
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
            }
        }
        return succeeded;
    }
}
