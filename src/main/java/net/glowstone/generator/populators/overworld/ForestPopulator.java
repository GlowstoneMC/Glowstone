package net.glowstone.generator.populators.overworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.DoubleTallPlant;
import net.glowstone.generator.objects.trees.BirchTree;
import net.glowstone.generator.objects.trees.GenericTree;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class ForestPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.FOREST, Biome.WOODED_HILLS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(GenericTree::new, 4),
        new TreeDecoration(BirchTree::new, 1)};
    private static final Material[] DOUBLE_PLANTS = {Material.LILAC,
        Material.ROSE_BUSH, Material.PEONY};

    protected int doublePlantLoweringAmount = 3;

    /**
     * Creates a populator adapted for a basic forest (oaks and birches).
     */
    public ForestPopulator() {
        doublePlantDecorator.setAmount(0);
        treeDecorator.setAmount(10);
        treeDecorator.setTrees(TREES);
        tallGrassDecorator.setAmount(2);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        int sourceX = chunk.getX() << 4;
        int sourceZ = chunk.getZ() << 4;
        int amount = random.nextInt(5) - doublePlantLoweringAmount;
        int i = 0;
        while (i < amount) {
            for (int j = 0; j < 5; j++, i++) {
                int x = sourceX + random.nextInt(16);
                int z = sourceZ + random.nextInt(16);
                int y = random.nextInt(world.getHighestBlockYAt(x, z) + 32);
                Material species = DOUBLE_PLANTS[random.nextInt(DOUBLE_PLANTS.length)];
                if (new DoubleTallPlant(species).generate(world, random, x, y, z)) {
                    i++;
                    break;
                }
            }
        }

        super.populateOnGround(world, random, chunk);
    }
}
