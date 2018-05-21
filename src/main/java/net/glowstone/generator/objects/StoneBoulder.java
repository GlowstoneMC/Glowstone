package net.glowstone.generator.objects;

import com.google.common.collect.ImmutableSortedSet;
import java.util.Random;
import java.util.SortedSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;

public class StoneBoulder implements TerrainObject {

    private static final SortedSet<Material> GROUND_TYPES = ImmutableSortedSet
            .of(Material.GRASS, Material.DIRT, Material.STONE);

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean groundReached = false;
        while (sourceY > 3) {
            sourceY--;
            Block block = world.getBlockAt(sourceX, sourceY, sourceZ);
            if (block.isEmpty()) {
                continue;
            }
            if (GROUND_TYPES.contains(block.getType())) {
                groundReached = true;
                sourceY++;
                break;
            }
        }
        if (!groundReached || !world.getBlockAt(sourceX, sourceY, sourceZ).isEmpty()) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            int radiusX = random.nextInt(2);
            int radiusZ = random.nextInt(2);
            int radiusY = random.nextInt(2);
            float f = (radiusX + radiusZ + radiusY) * 0.333F + 0.5F;
            float fsquared = f * f;
            for (int x = -radiusX; x <= radiusX; x++) {
                float xsquared = x * x;
                for (int z = -radiusZ; z <= radiusZ; z++) {
                    float zsquared = z * z;
                    for (int y = -radiusY; y <= radiusY; y++) {
                        if (xsquared + zsquared + y * y > fsquared) {
                            continue;
                        }
                        BlockState state = world
                                .getBlockAt(sourceX + x, sourceY + y, sourceZ + z).getState();
                        if (!TerrainObject.killPlantAbove(state.getBlock())) {
                            // FIXME: Is it a bug to suppress the cobblestone beneath where a plant
                            // previously stood?!
                            state.setType(Material.MOSSY_COBBLESTONE);
                            state.setData(new MaterialData(Material.MOSSY_COBBLESTONE));
                            state.update(true);
                        }
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
