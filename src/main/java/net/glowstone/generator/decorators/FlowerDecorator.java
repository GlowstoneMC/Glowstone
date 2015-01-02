package net.glowstone.generator.decorators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class FlowerDecorator extends BlockDecorator {

    private final List<FlowerDecoration> defaultFlowers = new ArrayList<FlowerDecoration>();
    private final Map<Biome, List<FlowerDecoration>> biomesFlowers = new HashMap<Biome, List<FlowerDecoration>>();

    public final FlowerDecorator setDefaultFlowerWeight(int weight, Flower flower) {
        defaultFlowers.add(new FlowerDecoration(flower, weight));
        return this;
    }

    public final FlowerDecorator setFlowerWeight(int weight, Flower flower, Biome... biomes) {
        for (Biome biome : biomes) {
            if (biomesFlowers.containsKey(biome)) {
                biomesFlowers.get(biome).add(new FlowerDecoration(flower, weight));
            } else {
                final List<FlowerDecoration> decorations = new ArrayList<FlowerDecoration>();
                decorations.add(new FlowerDecoration(flower, weight));
                biomesFlowers.put(biome, decorations);
            }
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ)  + 32);

        // the flower can change on each decoration pass
        Flower flower = null;
        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesFlowers.containsKey(biome)) {
            flower = getRandomFlower(random, biomesFlowers.get(biome));
        } else {
            flower = getRandomFlower(random, defaultFlowers);
        }
        if (flower == null) {
            return;
        }

        for (int i = 0; i < 64; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (y < 255 && world.getBlockAt(x, y, z).getType() == Material.AIR &&
                    world.getBlockAt(x, y - 1, z).getType() == Material.GRASS) {
                if (flower.isDoublePlant() && world.getBlockAt(x, y + 1, z).getType() != Material.AIR) {
                    continue;
                }
                final Block block = world.getBlockAt(x, y, z);
                block.setType(flower.getType());
                block.setData((byte) flower.getData());
                if (flower.isDoublePlant()) {
                    world.getBlockAt(x, y + 1, z).setType(flower.getType());
                    world.getBlockAt(x, y + 1, z).setData((byte) 8);
                }
            }
        }
    }

    private Flower getRandomFlower(Random random, List<FlowerDecoration> decorations) {
        int totalWeight = 0;
        for (FlowerDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (FlowerDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getFlower();
            }
        }
        return null;
    }

    public static enum Flower {

        DANDELION(Material.YELLOW_FLOWER, 0),
        POPPY(Material.RED_ROSE, 0),
        BLUE_ORCHID(Material.RED_ROSE, 1),
        ALLIUM(Material.RED_ROSE, 2),
        HOUSTONIA(Material.RED_ROSE, 3),
        TULIP_RED(Material.RED_ROSE, 4),
        TULIP_ORANGE(Material.RED_ROSE, 5),
        TULIP_WHITE(Material.RED_ROSE, 6),
        TULIP_PINK(Material.RED_ROSE, 7),
        OXEYE_DAISY(Material.RED_ROSE, 8),

        SUNFLOWER(Material.DOUBLE_PLANT, 0, true),
        LILAC(Material.DOUBLE_PLANT, 1, true),
        ROSE_BUSH(Material.DOUBLE_PLANT, 4, true),
        PEONIA(Material.DOUBLE_PLANT, 5, true);

        private final Material type;
        private final int data;
        private final boolean doublePlant;

        private Flower(Material type, int data) {
            this(type, data, false);
        }

        private Flower(Material type, int data, boolean doublePlant) {
            this.type = type;
            this.data = data;
            this.doublePlant = doublePlant;
        }

        public Material getType() {
            return type;
        }

        public int getData() {
            return data;
        }

        public boolean isDoublePlant() {
            return doublePlant;
        }
    }

    public static class FlowerDecoration {

        private final Flower flower;
        private final int weight;

        public FlowerDecoration(Flower flower, int weight) {
            this.flower = flower;
            this.weight = weight;
        }

        public Flower getFlower() {
            return flower;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
