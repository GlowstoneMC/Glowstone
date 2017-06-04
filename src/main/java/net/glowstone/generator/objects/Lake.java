package net.glowstone.generator.objects;

import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.material.types.DoublePlantSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.DoublePlant;

import java.util.Arrays;
import java.util.Random;

public class Lake {
    private static final double MAX_DIAMETER = 16.0D;
    private static final double MAX_HEIGHT = 8.0D;
    private static final int MAX_BLOCKS = (int) (MAX_DIAMETER * MAX_DIAMETER * MAX_HEIGHT);
    private static final Material[] PLANT_TYPES = {Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE,
            Material.DOUBLE_PLANT, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM};
    private static final Biome[] MYCEL_BIOMES = {Biome.MUSHROOM_ISLAND, Biome.MUSHROOM_ISLAND_SHORE};
    private final Material type;

    public Lake(Material type) {
        this.type = type;
    }

    public void generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        sourceY -= (int) MAX_HEIGHT / 2;

        byte[] lakeMap = new byte[MAX_BLOCKS];
        for (int n = 0; n < random.nextInt(4) + 4; n++) {
            double sizeX = random.nextDouble() * 6.0D + 3;
            double sizeY = random.nextDouble() * 4.0D + 2;
            double sizeZ = random.nextDouble() * 6.0D + 3;
            double dX = random.nextDouble() * (MAX_DIAMETER - sizeX - 2) + 1 + sizeX / 2.0D;
            double dY = random.nextDouble() * (MAX_HEIGHT - sizeY - 4) + 2 + sizeY / 2.0D;
            double dZ = random.nextDouble() * (MAX_DIAMETER - sizeZ - 2) + 1 + sizeZ / 2.0D;
            for (int x = 1; x < (int) MAX_DIAMETER - 1; x++) {
                for (int z = 1; z < (int) MAX_DIAMETER - 1; z++) {
                    for (int y = 1; y < (int) MAX_HEIGHT - 1; y++) {
                        double nX = (x - dX) / (sizeX / 2.0D);
                        nX *= nX;
                        double nY = (y - dY) / (sizeY / 2.0D);
                        nY *= nY;
                        double nZ = (z - dZ) / (sizeZ / 2.0D);
                        nZ *= nZ;
                        if (nX + nY + nZ < 1.0D) {
                            setLakeBlock(lakeMap, x, y, z);
                        }
                    }
                }
            }
        }

        if (!canPlace(lakeMap, world, sourceX, sourceY, sourceZ)) {
            return;
        }

        Biome biome = world.getBiome(sourceX + 8 + (int) MAX_DIAMETER / 2, sourceZ + 8 + (int) MAX_DIAMETER / 2);
        boolean mycelBiome = Arrays.asList(MYCEL_BIOMES).contains(biome);

        for (int x = 0; x < (int) MAX_DIAMETER; x++) {
            for (int z = 0; z < (int) MAX_DIAMETER; z++) {
                for (int y = 0; y < (int) MAX_HEIGHT; y++) {
                    if (isLakeBlock(lakeMap, x, y, z)) {
                        Material type = this.type;
                        Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        Block blockAbove = block.getRelative(BlockFace.UP);
                        if (block.getType() == Material.DIRT &&
                                (blockAbove.getType() == Material.LOG || blockAbove.getType() == Material.LOG_2) ||
                                block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
                            continue;
                        }
                        if (y >= (int) MAX_HEIGHT / 2) {
                            type = Material.AIR;
                            for (Material mat : PLANT_TYPES) {
                                if (blockAbove.getType() == mat) {
                                    if (mat == Material.DOUBLE_PLANT) {
                                        Block blockAboveBlock = blockAbove.getRelative(BlockFace.UP);
                                        if (blockAboveBlock.getState().getData() instanceof DoublePlant &&
                                                ((DoublePlant) blockAboveBlock.getState().getData()).getSpecies() == DoublePlantSpecies.PLANT_APEX) {
                                            blockAboveBlock.setType(Material.AIR);
                                        }
                                    }
                                    blockAbove.setType(Material.AIR);
                                    break;
                                }
                            }
                            if (this.type == Material.STATIONARY_WATER && (block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE)) {
                                type = block.getType();
                            }
                        } else if (y == MAX_HEIGHT / 2 - 1) {
                            if (type == Material.STATIONARY_WATER && GlowBiomeClimate.isCold(world.getBiome(sourceX + x, sourceZ + z),
                                    sourceX + x, y, sourceZ + z)) {
                                type = Material.ICE;
                            }
                        }
                        block.setType(type);
                    }
                }
            }
        }

        for (int x = 0; x < (int) MAX_DIAMETER; x++) {
            for (int z = 0; z < (int) MAX_DIAMETER; z++) {
                for (int y = (int) MAX_HEIGHT / 2; y < (int) MAX_HEIGHT; y++) {
                    if (isLakeBlock(lakeMap, x, y, z)) {
                        Block block = world.getBlockAt(sourceX + x, sourceY + y - 1, sourceZ + z);
                        if (block.getType() == Material.DIRT && !block.getRelative(BlockFace.UP).getType().isOccluding() &&
                                block.getRelative(BlockFace.UP).getLightLevel() > 0) {
                            block.setType(mycelBiome ? Material.MYCEL : Material.GRASS);
                        }
                    }
                }
            }
        }
    }

    private boolean canPlace(byte[] lakeMap, World world, int sourceX, int sourceY, int sourceZ) {
        for (int x = 0; x < MAX_DIAMETER; x++) {
            for (int z = 0; z < MAX_DIAMETER; z++) {
                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (!isLakeBlock(lakeMap, x, y, z) &&
                            (x < MAX_DIAMETER - 1 && isLakeBlock(lakeMap, x + 1, y, z) ||
                                    x > 0 && isLakeBlock(lakeMap, x - 1, y, z) ||
                                    z < MAX_DIAMETER - 1 && isLakeBlock(lakeMap, x, y, z + 1) ||
                                    z > 0 && isLakeBlock(lakeMap, x, y, z - 1) ||
                                    z < MAX_HEIGHT - 1 && isLakeBlock(lakeMap, x, y + 1, z) ||
                                    z > 0 && isLakeBlock(lakeMap, x, y - 1, z))) {
                        Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (y >= MAX_HEIGHT / 2 && (block.isLiquid() || block.getType() == Material.ICE)) {
                            return false; // there's already some liquids above
                        } else if (y < MAX_HEIGHT / 2 && !block.getType().isSolid() && block.getType() != type) {
                            return false; // bottom must be solid and do not overlap with another liquid type
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isLakeBlock(byte[] lakeMap, int x, int y, int z) {
        return lakeMap[(x * (int) MAX_DIAMETER + z) * (int) MAX_HEIGHT + y] != 0;
    }

    private void setLakeBlock(byte[] lakeMap, int x, int y, int z) {
        lakeMap[(x * (int) MAX_DIAMETER + z) * (int) MAX_HEIGHT + y] = 1;
    }
}
