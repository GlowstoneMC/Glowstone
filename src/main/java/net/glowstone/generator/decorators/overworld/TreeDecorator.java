package net.glowstone.generator.decorators.overworld;

import lombok.Data;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.trees.GenericTree;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class TreeDecorator extends BlockDecorator {

    private List<TreeDecoration> trees;

    public final void setTrees(TreeDecoration... trees) {
        this.trees = Arrays.asList(trees);
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        int treeAmount = amount;
        if (random.nextInt(10) == 0) {
            treeAmount++;
        }
        for (int i = 0; i < treeAmount; i++) {
            decorate(world, random, chunk);
        }
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        Block sourceBlock = world
                .getBlockAt(sourceX, world.getHighestBlockYAt(sourceX, sourceZ), sourceZ);

        BiFunction<Random, BlockStateDelegate, ? extends GenericTree> ctor
                = getRandomTree(random, trees);
        if (ctor != null) {
            BlockStateDelegate delegate = new BlockStateDelegate();
            GenericTree tree;
            try {
                tree = ctor.apply(random, delegate);
            } catch (Exception ex) {
                tree = new GenericTree(random, delegate);
            }
            if (tree.generate(sourceBlock.getLocation())) {
                delegate.updateBlockStates();
            }
        }
    }

    private BiFunction<Random, BlockStateDelegate, ? extends GenericTree>
            getRandomTree(Random random, List<TreeDecoration> decorations) {
        int totalWeight = 0;
        for (TreeDecoration decoration : decorations) {
            totalWeight += decoration.getWeight();
        }
        int weight = random.nextInt(totalWeight);
        for (TreeDecoration decoration : decorations) {
            weight -= decoration.getWeight();
            if (weight < 0) {
                return decoration.getConstructor();
            }
        }
        return null;
    }

    @Data
    public static final class TreeDecoration {
        private final BiFunction<Random, BlockStateDelegate, ? extends GenericTree> constructor;
        private final int weight;
    }
}
