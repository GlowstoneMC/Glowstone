package net.glowstone.generator.decorators.overworld;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.util.BlockStateDelegate;

public class TreeDecorator extends BlockDecorator {

    private final Map<Biome, List<TreeDecoration>> biomesTrees = new HashMap<>();

    public final TreeDecorator setTreeWeight(int weight, Class<? extends GenericTree> tree, Biome... biomes) {
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
    public void populate(World world, Random random, Chunk chunk) {
        int amount = getBiomeAmount(world, chunk);
        if (random.nextInt(10) == 0) {
            amount++;
        }
        for (int i = 0; i < amount; i++) {
            decorate(world, random, chunk);
        }
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        final Block sourceBlock = world.getBlockAt(sourceX, world.getHighestBlockYAt(sourceX, sourceZ), sourceZ);

        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesTrees.containsKey(biome)) {
            final Class<? extends GenericTree> clazz = getRandomTree(random, biomesTrees.get(biome));
            if (clazz != null) {
                final BlockStateDelegate delegate = new BlockStateDelegate();
                GenericTree tree;
                try {
                    final Constructor<? extends GenericTree> c = clazz.getConstructor(Random.class, Location.class, BlockStateDelegate.class);
                    tree = c.newInstance(random, sourceBlock.getLocation(), delegate);
                } catch (Exception ex) {
                    tree = new GenericTree(random, sourceBlock.getLocation(), delegate);
                }
                if (tree.generate()) {
                    delegate.updateBlockStates();
                }
            }
        }
    }

    private Class<? extends GenericTree> getRandomTree(Random random, List<TreeDecoration> decorations) {
        int totalWeight = 0;
        for (TreeDecoration decoration : decorations) {
            totalWeight += decoration.getWeigth();
        }
        int weight = random.nextInt(totalWeight);
        for (TreeDecoration decoration : decorations) {
            weight -= decoration.getWeigth();
            if (weight < 0) {
                return decoration.getTree();
            }
        }
        return null;
    }

    public static class TreeDecoration {

        private final Class<? extends GenericTree> tree;
        private final int weight;

        public TreeDecoration(Class<? extends GenericTree> tree, int weight) {
            this.tree = tree;
            this.weight = weight;
        }

        public Class<? extends GenericTree> getTree() {
            return tree;
        }

        public int getWeigth() {
            return weight;
        }
    }
}
