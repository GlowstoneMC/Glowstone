package net.glowstone.generator.decorators.overworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import net.glowstone.generator.TreeGenerator;
import net.glowstone.generator.decorators.BlockDecorator;

public class TreeDecorator extends BlockDecorator {

    private final Map<Biome, List<TreeDecoration>> biomesTrees = new HashMap<>();

    public final TreeDecorator setTreeWeight(int weight, TreeType tree, Biome... biomes) {
        for (Biome biome : biomes) {
            if (biomesTrees.containsKey(biome)) {
                biomesTrees.get(biome).add(new TreeDecoration(tree, weight));
            } else {
                final List<TreeDecoration> decorations = new ArrayList<>();
                decorations.add(new TreeDecoration(tree, weight));
                biomesTrees.put(biome, decorations);
            }
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        final Block sourceBlock = world.getBlockAt(sourceX, world.getHighestBlockYAt(sourceX, sourceZ) - 1, sourceZ);

        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesTrees.containsKey(biome)) {
            final TreeType type = getRandomTree(random, biomesTrees.get(biome));
            if (type != null) {
                new TreeGenerator().generate(random, sourceBlock.getLocation().add(0, 1, 0), type);
            }
        }
    }

    private TreeType getRandomTree(Random random, List<TreeDecoration> decorations) {
        int totalWeight = 0;
        for (TreeDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (TreeDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getTreeType();
            }
        }
        return null;
    }

    public static class TreeDecoration {

        private final TreeType tree;
        private final int weight;

        public TreeDecoration(TreeType tree, int weight) {
            this.tree = tree;
            this.weight = weight;
        }

        public TreeType getTreeType() {
            return tree;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
