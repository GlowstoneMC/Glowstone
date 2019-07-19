package net.glowstone.generator.populators.overworld;

import net.glowstone.generator.decorators.EntityDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.objects.trees.RedwoodTree;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SnowyTundraPopulator extends BiomePopulator {

    private static final Biome[] BIOMES = {Biome.SNOWY_TUNDRA, Biome.SNOWY_MOUNTAINS};
    private static final TreeDecoration[] TREES = {new TreeDecoration(RedwoodTree::new, 1)};

    /**
     * Creates a populator specialized for snowy tundra and snowy mountains, with rabbits, polar bears
     * and no vegetation.
     */
    public SnowyTundraPopulator() {
        treeDecorator.setAmount(0);
        treeDecorator.setTrees(TREES);
        flowerDecorator.setAmount(0);
        entityDecorators.clear();
        EntityDecorator rabbitDecorator = new EntityDecorator(EntityType.RABBIT);
        rabbitDecorator.setGroupSize(2, 3);
        entityDecorators.add(rabbitDecorator);
        EntityDecorator bearDecorator = new EntityDecorator(EntityType.POLAR_BEAR);
        bearDecorator.setRarity(0.01f);
        bearDecorator.setGroupSize(1, 2);
        entityDecorators.add(bearDecorator);
    }

    @Override
    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(BIOMES));
    }
}
