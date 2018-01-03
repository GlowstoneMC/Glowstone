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

public class StoneBoulder implements TerrainObject {

    private static final Material[] GROUND_TYPES = {Material.GRASS, Material.DIRT, Material.STONE};
    private static final SortedSet<Material> PLANT_TYPES = ImmutableSortedSet
            .of(Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE,
                    Material.DOUBLE_PLANT, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM);

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean groundReached = false;
        while (!groundReached && sourceY > 3) {
            Block block = world.getBlockAt(sourceX, sourceY - 1, sourceZ);
            if (!block.isEmpty()) {
                for (Material mat : GROUND_TYPES) {
                    if (mat == block.getType()) {
                        groundReached = true;
                        sourceY++;
                        break;
                    }
                }
            }
            sourceY--;
        }
        if (!groundReached || !world.getBlockAt(sourceX, sourceY, sourceZ).isEmpty()) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            int radiusX = random.nextInt(2);
            int radiusZ = random.nextInt(2);
            int radiusY = random.nextInt(2);
            float f = (radiusX + radiusZ + radiusY) * 0.333F + 0.5F;
            for (int x = -radiusX; x <= radiusX; x++) {
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    for (int y = -radiusY; y <= radiusY; y++) {
                        if (x * x + z * z + y * y > f * f) {
                            continue;
                        }
                        BlockState state = world
                                .getBlockAt(sourceX + x, sourceY + y, sourceZ + z).getState();
                        Block blockAbove = state.getBlock().getRelative(BlockFace.UP);
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
                            break;
                        }
                        state.setType(Material.MOSSY_COBBLESTONE);
                        state.setData(new MaterialData(Material.MOSSY_COBBLESTONE));
                        state.update(true);
                    }
                }
            }
            sourceX += random.nextInt(4) - 1;
            sourceZ += random.nextInt(4) - 1;
            sourceY -= random.nextInt(2);
        }
        return true;
    }
}
