package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.overworld.MelonDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BigOakTree;
import net.glowstone.generator.objects.trees.CocoaTree;
import net.glowstone.generator.objects.trees.JungleBush;
import net.glowstone.generator.objects.trees.MegaJungleTree;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class JunglePopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.JUNGLE, Biome.JUNGLE_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(BigOakTree.class, 10),
            new TreeDecoration(JungleBush.class, 50), new TreeDecoration(MegaJungleTree.class, 15),
            new TreeDecoration(CocoaTree.class, 30)};

    private final MelonDecorator melonDecorator = new MelonDecorator();

    public JunglePopulator() {
        super();
        treeDecorator.setAmount(65);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(4);
        tallGrassDecorator.setAmount(25);
        tallGrassDecorator.setFernDensity(0.25D);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {

        int sourceX = (chunk.getX() << 4);
        int sourceZ = (chunk.getZ() << 4);

        for (int i = 0; i < 7; i++) {
            int x = sourceX + random.nextInt(16);
            int z = sourceZ + random.nextInt(16);
            int y = world.getHighestBlockYAt(x, z);
            final Block sourceBlock = world.getBlockAt(x, y, z);
            final BlockStateDelegate delegate = new BlockStateDelegate();
            final JungleBush bush = new JungleBush(random, sourceBlock.getLocation(), delegate);
            if (bush.generate()) {
                delegate.updateBlockStates();
            }
        }

        super.populateOnGround(world, random, chunk);
        melonDecorator.populate(world, random, chunk);
    }
}
