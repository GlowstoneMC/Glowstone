package net.glowstone.generator.objects;

import java.util.Arrays;
import java.util.Random;
import net.glowstone.constants.GlowBiomeClimate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Lake implements TerrainObject {

    private static final double MAX_DIAMETER = 16.0D;
    private static final double MAX_HEIGHT = 8.0D;
    private static final int MAX_BLOCKS = (int) (MAX_DIAMETER * MAX_DIAMETER * MAX_HEIGHT);
    private static final Biome[] MYCEL_BIOMES = {Biome.MUSHROOM_FIELDS,
        Biome.MUSHROOM_FIELD_SHORE};
    private final Material type;

    public Lake(Material type) {
        this.type = type;
    }

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        boolean succeeded = false;
        sourceY -= (int) MAX_HEIGHT / 2;

        byte[] lakeMap = new byte[MAX_BLOCKS];
        for (int n = 0; n < random.nextInt(4) + 4; n++) {
            double sizeX = random.nextDouble() * 6.0D + 3;
            double sizeY = random.nextDouble() * 4.0D + 2;
            double sizeZ = random.nextDouble() * 6.0D + 3;
            double dx = random.nextDouble() * (MAX_DIAMETER - sizeX - 2) + 1 + sizeX / 2.0D;
            double dy = random.nextDouble() * (MAX_HEIGHT - sizeY - 4) + 2 + sizeY / 2.0D;
            double dz = random.nextDouble() * (MAX_DIAMETER - sizeZ - 2) + 1 + sizeZ / 2.0D;
            for (int x = 1; x < (int) MAX_DIAMETER - 1; x++) {
                for (int z = 1; z < (int) MAX_DIAMETER - 1; z++) {
                    for (int y = 1; y < (int) MAX_HEIGHT - 1; y++) {
                        double nx = (x - dx) / (sizeX / 2.0D);
                        nx *= nx;
                        double ny = (y - dy) / (sizeY / 2.0D);
                        ny *= ny;
                        double nz = (z - dz) / (sizeZ / 2.0D);
                        nz *= nz;
                        if (nx + ny + nz < 1.0D) {
                            setLakeBlock(lakeMap, x, y, z);
                            succeeded = true;
                        }
                    }
                }
            }
        }

        if (!canPlace(lakeMap, world, sourceX, sourceY, sourceZ)) {
            return succeeded;
        }

        Biome biome = world
                .getBiome(
                        sourceX + 8 + (int) MAX_DIAMETER / 2, sourceZ + 8 + (int) MAX_DIAMETER / 2);
        boolean mycelBiome = Arrays.asList(MYCEL_BIOMES).contains(biome);

        for (int x = 0; x < (int) MAX_DIAMETER; x++) {
            for (int z = 0; z < (int) MAX_DIAMETER; z++) {
                for (int y = 0; y < (int) MAX_HEIGHT; y++) {
                    if (!isLakeBlock(lakeMap, x, y, z)) {
                        continue;
                    }
                    Material type = this.type;
                    Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                    Block blockAbove = block.getRelative(BlockFace.UP);
                    Material blockType = block.getType();
                    Material blockAboveType = blockAbove.getType();
                    // TODO: 1.13 log types
                    if (blockType == Material.DIRT
                                    && (blockAboveType == Material.LEGACY_LOG
                                    || blockAboveType == Material.LEGACY_LOG_2)
                            || blockType == Material.LEGACY_LOG
                            || blockType == Material.LEGACY_LOG_2) {
                        continue;
                    }
                    if (y >= (int) MAX_HEIGHT / 2) {
                        type = Material.AIR;
                        if (TerrainObject.killPlantAbove(block)) {
                            break;
                        }
                        if (this.type == Material.WATER && (
                                blockType == Material.ICE
                                        || blockType == Material.PACKED_ICE)) {
                            type = blockType;
                        }
                    } else if (y == MAX_HEIGHT / 2 - 1) {
                        if (type == Material.WATER && GlowBiomeClimate
                                .isCold(world.getBiome(sourceX + x, sourceZ + z),
                                        sourceX + x, y, sourceZ + z)) {
                            type = Material.ICE;
                        }
                    }
                    block.setType(type);
                }
            }
        }

        for (int x = 0; x < (int) MAX_DIAMETER; x++) {
            for (int z = 0; z < (int) MAX_DIAMETER; z++) {
                for (int y = (int) MAX_HEIGHT / 2; y < (int) MAX_HEIGHT; y++) {
                    if (!isLakeBlock(lakeMap, x, y, z)) {
                        continue;
                    }
                    Block block = world.getBlockAt(sourceX + x, sourceY + y - 1, sourceZ + z);
                    Block blockAbove = block.getRelative(BlockFace.UP);
                    if (block.getType() == Material.DIRT
                            && !blockAbove.getType().isOccluding()
                            && blockAbove.getLightLevel() > 0) {
                        block.setType(mycelBiome ? Material.MYCELIUM : Material.GRASS_BLOCK);
                    }
                }
            }
        }
        return succeeded;
    }

    private boolean canPlace(byte[] lakeMap, World world, int sourceX, int sourceY, int sourceZ) {
        for (int x = 0; x < MAX_DIAMETER; x++) {
            for (int z = 0; z < MAX_DIAMETER; z++) {
                for (int y = 0; y < MAX_HEIGHT; y++) {
                    if (isLakeBlock(lakeMap, x, y, z)
                            || (((x >= (MAX_DIAMETER - 1)) || !isLakeBlock(lakeMap, x + 1, y, z))
                            && ((x <= 0) || !isLakeBlock(lakeMap, x - 1, y, z))
                            && ((z >= (MAX_DIAMETER - 1)) || !isLakeBlock(lakeMap, x, y,
                            z + 1))
                            && ((z <= 0) || !isLakeBlock(lakeMap, x, y, z - 1))
                            && ((z >= (MAX_HEIGHT - 1)) || !isLakeBlock(lakeMap, x, y + 1, z))
                            && ((z <= 0) || !isLakeBlock(lakeMap, x, y - 1, z)))) {
                        continue;
                    }
                    Block block = world.getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                    if (y >= MAX_HEIGHT / 2 && (block.isLiquid()
                            || block.getType() == Material.ICE)) {
                        return false; // there's already some liquids above
                    } else if (y < MAX_HEIGHT / 2 && !block.getType().isSolid()
                            && block.getType() != type) {
                        return false;
                        // bottom must be solid and do not overlap with another liquid type
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
