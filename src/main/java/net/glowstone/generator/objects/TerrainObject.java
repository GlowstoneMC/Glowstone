package net.glowstone.generator.objects;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Random;
import java.util.SortedSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DoublePlantSpecies;

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
    SortedSet<Material> PLANT_TYPES = ImmutableSortedSet
            .of(Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE,
                    Material.DOUBLE_PLANT, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM);

    /**
     * Removes the grass, shrub, flower or mushroom directly above the given block, if present. Does
     * not drop an item.
     *
     * @param block the block to update
     * @return true if a plant was removed; false if none was present
     */
    static boolean killPlantAbove(Block block) {
        Block blockAbove = block.getRelative(BlockFace.UP);
        Material mat = blockAbove.getType();
        if (PLANT_TYPES.contains(mat)) {
            if (mat == Material.DOUBLE_PLANT) {
                MaterialData dataAbove = blockAbove.getState().getData();
                if (dataAbove instanceof DoublePlant
                        && ((DoublePlant) dataAbove).getSpecies()
                        == DoublePlantSpecies.PLANT_APEX) {
                    blockAbove.getRelative(BlockFace.UP)
                            .setType(Material.AIR);
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
     * @param world the world to generate in
     * @param random the PRNG that will choose the size and a few details of the shape
     * @param sourceX the base X coordinate
     * @param sourceY the base Y coordinate
     * @param sourceZ the base Z coordinate
     * @return true if successfully generated
     */
    boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ);
}
