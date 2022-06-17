package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.EntityDecorator;
import net.glowstone.generator.decorators.overworld.MushroomDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.BrownMushroomTree;
import net.glowstone.generator.objects.trees.RedMushroomTree;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class MushroomFieldsPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.MUSHROOM_FIELDS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedMushroomTree::new, 1),
        new TreeDecoration(BrownMushroomTree::new, 1)};

    protected final MushroomDecorator islandBrownMushroomDecorator = new MushroomDecorator(
        Material.BROWN_MUSHROOM);
    protected final MushroomDecorator islandRedMushroomDecorator = new MushroomDecorator(
        Material.RED_MUSHROOM);

    /**
     * Creates a populator for the mushroom fields biome.
     */
    public MushroomFieldsPopulator() {
        treeDecorator.setAmount(1);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(0);
        tallGrassDecorator.setAmount(0);
        islandBrownMushroomDecorator.setAmount(1);
        islandBrownMushroomDecorator.setUseFixedHeightRange();
        islandBrownMushroomDecorator.setDensity(0.25D);
        islandRedMushroomDecorator.setAmount(1);
        islandRedMushroomDecorator.setDensity(0.125D);
        entityDecorators.clear();
        EntityDecorator mushroomDecorator = new EntityDecorator(EntityType.MUSHROOM_COW);
        mushroomDecorator.setGroupSize(4, 8);
        entityDecorators.add(mushroomDecorator);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }

    @Override
    public void populateOnGround(World world, Random random, Chunk chunk) {
        super.populateOnGround(world, random, chunk);
        islandBrownMushroomDecorator.populate(world, random, chunk);
        islandRedMushroomDecorator.populate(world, random, chunk);
    }
}
