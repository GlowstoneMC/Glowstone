package net.glowstone.generator.populators;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.util.Random;

/**
 * A BlockPopulator that places dungeons around the map.
 */
public class DungeonPopulator extends BlockPopulator {

    @Override
    public void populate(World world, Random random, Chunk source) {
        SimplexNoiseGenerator noise = new SimplexNoiseGenerator(world);
        ChunkSnapshot snapshot = source.getChunkSnapshot();

        // Randomly turn exposed stone to treasure
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = snapshot.getHighestBlockYAt(x, z);
                Block block = source.getBlock(x, y - 1, z);

                if (block.getType() == Material.STONE
                        && random.nextInt(1024) == 0) {
                    placeChest(random, block);
                }
            }
        }

        // Go go dungeons
        double density = noise.noise(source.getX(), source.getZ());
        if (density > 0.8) {
            int roomCount = (int) (density * 10) - 3;

            for (int i = 0; i < roomCount; i++) {
                if (random.nextBoolean()) {
                    int x = (source.getX() << 4) + random.nextInt(16);
                    int z = (source.getZ() << 4) + random.nextInt(16);
                    int y = 12 + random.nextInt(22);

                    int sizeX = random.nextInt(12) + 5;
                    int sizeY = random.nextInt(6) + 4;
                    int sizeZ = random.nextInt(12) + 5;

                    generateRoom(x, y, z, sizeX, sizeY, sizeZ, world, random);
                }
            }
        }
    }

    private static void generateRoom(int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, World world, Random random) {
        // Fill with air
        for (int x = posX; x < posX + sizeX; x++) {
            for (int y = posY; y < posY + sizeY; y++) {
                for (int z = posZ; z < posZ + sizeZ; z++) {
                    placeBlock(world, x, y, z, Material.AIR);
                }
            }
        }

        // Spawners
        int numSpawners = 1 + random.nextInt(2);
        for (int i = 0; i < numSpawners; ++i) {
            int x = posX + random.nextInt(sizeX);
            int z = posZ + random.nextInt(sizeZ);
            placeSpawner(random, world.getBlockAt(x, posY, z));
        }

        // Chests
        int numChests = numSpawners + random.nextInt(2);
        for (int i = 0; i < numChests; ++i) {
            int x = posX + random.nextInt(sizeX);
            int z = posZ + random.nextInt(sizeZ);
            placeChest(random, world.getBlockAt(x, posY, z));
        }

        for (int x = posX - 1; x <= posX + sizeX; x++) {
            for (int z = posZ - 1; z <= posZ + sizeZ; z++) {
                placeBlock(world, x, posY - 1, z, pickStone(random));
                placeBlock(world, x, posY + sizeY, z, pickStone(random));
            }
        }

        for (int y = posY - 1; y <= posY + sizeX; y++) {
            for (int z = posZ - 1; z <= posZ + sizeZ; z++) {
                placeBlock(world, posX - 1, y, z, pickStone(random));
                placeBlock(world, posX + sizeX, y, z, pickStone(random));
            }
        }

        for (int x = posX - 1; x <= posX + sizeX; x++) {
            for (int y = posY - 1; y <= posY + sizeY; y++) {
                placeBlock(world, x, y, posZ - 1, pickStone(random));
                placeBlock(world, x, y, posZ + sizeZ, pickStone(random));
            }
        }
    }

    private static Material pickStone(Random random) {
        return random.nextInt(6) == 0 ? Material.MOSSY_COBBLESTONE : Material.COBBLESTONE;
    }

    private static void placeSpawner(Random random, Block block) {
        CreatureType[] types = new CreatureType[]{
            CreatureType.SKELETON, CreatureType.ZOMBIE,
            CreatureType.CREEPER, CreatureType.SPIDER
        };

        block.setType(Material.MOB_SPAWNER);
        BlockState state = block.getState();
        if (state instanceof CreatureSpawner) {
            ((CreatureSpawner) state).setCreatureType(types[random.nextInt(types.length)]);
        }
    }

    private static void placeChest(Random random, Block block) {
        block.setType(Material.CHEST);
        BlockState state = block.getState();
        if (state instanceof Chest) {
            Inventory chest = ((Chest) state).getInventory();

            for (int i = 0; i < 5; i++) {
                chest.setItem(random.nextInt(chest.getSize()), getRandomTool(random, i));
                if (i < 5) {
                    chest.setItem(random.nextInt(chest.getSize()), getRandomArmor(random, i));
                }
            }

            chest.setItem(random.nextInt(chest.getSize()), getRandomOre(random));
        }
    }

    private static ItemStack getRandomOre(Random random) {
        int i = random.nextInt(255);
        int count = random.nextInt(63) + 1;

        if (i > 253) {
            return new ItemStack(Material.LAPIS_BLOCK, count);
        } else if (i > 230) {
            return new ItemStack(Material.DIAMOND_ORE, count);
        } else if (i > 190) {
            return new ItemStack(Material.GOLD_ORE, count);
        } else if (i > 150) {
            return new ItemStack(Material.IRON_ORE, count);
        } else {
            return new ItemStack(Material.COAL, count);
        }
    }

    private static ItemStack getRandomTool(Random random, int index) {
        // 0 = sword, 1 = spade, 2 = pickaxe, 3 = axe
        int i = random.nextInt(255);

        if (i > 245) { // Diamond
            return new ItemStack(276 + index, 1);
        } else if (i > 230) { // Gold
            return new ItemStack(283 + index, 1);
        } else if (i > 190) {
            if (index == 0) { // Iron sword
                return new ItemStack(267, 1);
            }
            // Iron items
            return new ItemStack(255 + index, 1);
        } else if (i > 150) { // Stone
            return new ItemStack(272 + index, 1);
        } else { // Wood
            return new ItemStack(268 + index, 1);
        }
    }

    private static ItemStack getRandomArmor(Random random, int index) {
        // 0 = helmet, 1 = chestplate, 2 = leggings, 3 = boots
        int i = random.nextInt(255);

        if (i > 245) { // Diamond
            return new ItemStack(310 + index, 1);
        } else if (i > 230) { // Chainmail
            return new ItemStack(302 + index, 1);
        } else if (i > 190) { // Gold
            return new ItemStack(314 + index, 1);
        } else if (i > 150) { // Iron
            return new ItemStack(306 + index, 1);
        } else { // Leather
            return new ItemStack(298 + index, 1);
        }
    }

    private static void placeBlock(World world, int x, int y, int z, Material mat) {
        if (canPlaceBlock(world, x, y, z) && mat != null) {
            world.getBlockAt(x, y, z).setType(mat);
        }
    }

    private static boolean canPlaceBlock(World world, int x, int y, int z) {
        Block block = world.getBlockAt(x, y, z);
        return !block.isLiquid() && block.getType() != Material.MOB_SPAWNER
                && block.getType() != Material.CHEST;
    }
    
}
