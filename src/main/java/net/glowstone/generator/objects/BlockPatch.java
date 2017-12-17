package net.glowstone.generator.objects;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.DoublePlant;
import org.bukkit.material.MaterialData;
import org.bukkit.material.types.DoublePlantSpecies;

public class BlockPatch {

    private static final int MIN_RADIUS = 2;
    private static final Material[] PLANT_TYPES = {Material.LONG_GRASS, Material.YELLOW_FLOWER,
        Material.RED_ROSE,
        Material.DOUBLE_PLANT, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM};
    private final Material type;
    private final int hRadius;
    private final int vRadius;
    private final Material[] overridables;

    public BlockPatch(Material type, int hRadius, int vRadius, Material... overridables) {
        this.type = type;
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        this.overridables = overridables;
    }

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        int n = random.nextInt(hRadius - MIN_RADIUS) + MIN_RADIUS;
        for (int x = sourceX - n; x <= sourceX + n; x++) {
            for (int z = sourceZ - n; z <= sourceZ + n; z++) {
                if ((x - sourceX) * (x - sourceX) + (z - sourceZ) * (z - sourceZ) <= n * n) {
                    for (int y = sourceY - vRadius; y <= sourceY + vRadius; y++) {
                        Block block = world.getBlockAt(x, y, z);
                        for (Material overridable : overridables) {
                            if (block.getType() == overridable) {
                                Block blockAbove = block.getRelative(BlockFace.UP);
                                for (Material mat : PLANT_TYPES) {
                                    if (blockAbove.getType() == mat) {
                                        if (mat == Material.DOUBLE_PLANT && blockAbove.getState()
                                            .getData() instanceof DoublePlant &&
                                            ((DoublePlant) blockAbove.getState().getData())
                                                .getSpecies() == DoublePlantSpecies.PLANT_APEX) {
                                            blockAbove.getRelative(BlockFace.UP)
                                                .setType(Material.AIR);
                                        }
                                        blockAbove.setType(Material.AIR);
                                        break;
                                    }
                                }
                                BlockState state = block.getState();
                                state.setType(type);
                                state.setData(new MaterialData(type));
                                state.update(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
