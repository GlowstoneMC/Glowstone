package net.glowstone.generator.objects;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Random;
import java.util.SortedSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;

// TODO: Use this interface to reduce duplicate code in BlockPopulator subclasses.
// TODO: Refactor GenericTree to implement this class.

/**
 * A terrain feature that can randomly generate itself at specified locations, replacing blocks.
 */
@FunctionalInterface
public interface TerrainObject {
    /**
     * Plant block types.
     */
    SortedSet<Material> PLANT_TYPES = ImmutableSortedSet.of(
            // Flowers
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.SUNFLOWER,
            Material.LILAC,
            Material.ROSE_BUSH,
            Material.PEONY,
            // Grass
            Material.GRASS,
            Material.FERN,
            Material.TALL_GRASS,
            Material.LARGE_FERN
    );

    /**
     * Removes the grass, shrub, flower or mushroom directly above the given block, if present. Does
     * not drop an item.
     *
     * @param block the block to update
     * @return true if a plant was removed; false if none was present
     */
    static boolean killPlantAbove(Block block) {
        Block blockAbove = block.getRelative(BlockFace.UP);
        BlockData blockAboveData = blockAbove.getBlockData();
        Material mat = blockAboveData.getMaterial();
        if (PLANT_TYPES.contains(mat)) {
            if (blockAboveData instanceof Bisected && ((Bisected) blockAboveData).getHalf() == Bisected.Half.BOTTOM) {
                // Large plant
                Block plantTop = blockAbove.getRelative(BlockFace.UP);
                BlockData plantTopData = plantTop.getBlockData();
                if (plantTopData.getMaterial() == mat &&
                        ((Bisected) plantTopData).getHalf() == Bisected.Half.TOP) {
                    plantTop.setType(Material.AIR);
                }
            }
            blockAbove.setType(Material.AIR);
            return true;
        }
        return false;
    }

    /**
     * Generates this feature.
     *
     * @param world   the world to generate in
     * @param random  the PRNG that will choose the size and a few details of the shape
     * @param sourceX the base X coordinate
     * @param sourceY the base Y coordinate
     * @param sourceZ the base Z coordinate
     * @return true if successfully generated
     */
    boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ);
}
