package net.glowstone.generator.populators.overworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.glowstone.GlowWorld;
import net.glowstone.constants.GameRules;
import net.glowstone.generator.decorators.EntityDecorator;
import net.glowstone.generator.decorators.overworld.CactusDecorator;
import net.glowstone.generator.decorators.overworld.DeadBushDecorator;
import net.glowstone.generator.decorators.overworld.DesertWellDecorator;
import net.glowstone.generator.decorators.overworld.DoublePlantDecorator;
import net.glowstone.generator.decorators.overworld.FlowerDecorator;
import net.glowstone.generator.decorators.overworld.FlowerDecorator.FlowerDecoration;
import net.glowstone.generator.decorators.overworld.FlowingLiquidDecorator;
import net.glowstone.generator.decorators.overworld.LakeDecorator;
import net.glowstone.generator.decorators.overworld.MushroomDecorator;
import net.glowstone.generator.decorators.overworld.PumpkinDecorator;
import net.glowstone.generator.decorators.overworld.SugarCaneDecorator;
import net.glowstone.generator.decorators.overworld.SurfaceCaveDecorator;
import net.glowstone.generator.decorators.overworld.TallGrassDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator;
import net.glowstone.generator.decorators.overworld.TreeDecorator.TreeDecoration;
import net.glowstone.generator.decorators.overworld.UnderwaterDecorator;
import net.glowstone.generator.objects.FlowerType;
import net.glowstone.generator.objects.trees.BigOakTree;
import net.glowstone.generator.objects.trees.GenericTree;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;

public class BiomePopulator extends BlockPopulator {

    private static final TreeDecoration[] TREES = {new TreeDecoration(BigOakTree::new, 1),
        new TreeDecoration(GenericTree::new, 9)};
    private static final FlowerDecoration[] FLOWERS = {
        new FlowerDecoration(FlowerType.DANDELION, 2), new FlowerDecoration(FlowerType.POPPY, 1)};
    protected final LakeDecorator waterLakeDecorator = new LakeDecorator(Material.STATIONARY_WATER);
    protected final LakeDecorator lavaLakeDecorator = new LakeDecorator(Material.STATIONARY_LAVA);
    protected final DungeonPopulator dungeonPopulator = new DungeonPopulator();
    protected final OrePopulator orePopulator = new OrePopulator();
    protected final UnderwaterDecorator sandPatchDecorator = new UnderwaterDecorator(Material.SAND);
    protected final UnderwaterDecorator clayPatchDecorator = new UnderwaterDecorator(Material.CLAY);
    protected final UnderwaterDecorator gravelPatchDecorator = new UnderwaterDecorator(
        Material.GRAVEL);
    protected final DoublePlantDecorator doublePlantDecorator = new DoublePlantDecorator();
    protected final TreeDecorator treeDecorator = new TreeDecorator();
    protected final DesertWellDecorator desertWellDecorator = new DesertWellDecorator();
    protected final FlowerDecorator flowerDecorator = new FlowerDecorator();
    protected final TallGrassDecorator tallGrassDecorator = new TallGrassDecorator();
    protected final DeadBushDecorator deadBushDecorator = new DeadBushDecorator();
    protected final MushroomDecorator brownMushroomDecorator = new MushroomDecorator(
        Material.BROWN_MUSHROOM);
    protected final MushroomDecorator redMushroomDecorator = new MushroomDecorator(
        Material.RED_MUSHROOM);
    protected final SugarCaneDecorator sugarCaneDecorator = new SugarCaneDecorator();
    protected final PumpkinDecorator pumpkinDecorator = new PumpkinDecorator();
    protected final CactusDecorator cactusDecorator = new CactusDecorator();
    protected final FlowingLiquidDecorator flowingWaterDecorator = new FlowingLiquidDecorator(
        Material.WATER);
    protected final FlowingLiquidDecorator flowingLavaDecorator = new FlowingLiquidDecorator(
        Material.LAVA);
    protected final SurfaceCaveDecorator surfaceCaveDecorator = new SurfaceCaveDecorator();
    protected final List<EntityDecorator> entityDecorators = new ArrayList<>();
    private final List<BlockPopulator> inGroundPopulators = new ArrayList<>();
    private final List<BlockPopulator> onGroundPopulators = new ArrayList<>();

    /**
     * Creates a populator for lakes; dungeons; caves; ores; sand, gravel and clay patches; desert
     * wells; and vegetation.
     *
     * @param biome ignored
     */
    public BiomePopulator(Biome... biome) {
        inGroundPopulators.add(waterLakeDecorator);
        inGroundPopulators.add(lavaLakeDecorator);
        inGroundPopulators.add(surfaceCaveDecorator);
        inGroundPopulators.add(dungeonPopulator);
        inGroundPopulators.add(orePopulator);
        inGroundPopulators.add(sandPatchDecorator);
        inGroundPopulators.add(clayPatchDecorator);
        inGroundPopulators.add(gravelPatchDecorator);

        onGroundPopulators.add(doublePlantDecorator);
        onGroundPopulators.add(treeDecorator);
        onGroundPopulators.add(desertWellDecorator);
        onGroundPopulators.add(flowerDecorator);
        onGroundPopulators.add(tallGrassDecorator);
        onGroundPopulators.add(deadBushDecorator);
        onGroundPopulators.add(brownMushroomDecorator);
        onGroundPopulators.add(redMushroomDecorator);
        onGroundPopulators.add(sugarCaneDecorator);
        onGroundPopulators.add(pumpkinDecorator);
        onGroundPopulators.add(cactusDecorator);
        onGroundPopulators.add(flowingWaterDecorator);
        onGroundPopulators.add(flowingLavaDecorator);

        waterLakeDecorator.setAmount(1);
        lavaLakeDecorator.setAmount(1);
        surfaceCaveDecorator.setAmount(1);
        sandPatchDecorator.setAmount(3);
        sandPatchDecorator.setRadii(7, 2);
        sandPatchDecorator.setOverridableBlocks(Material.DIRT, Material.GRASS);
        clayPatchDecorator.setAmount(1);
        clayPatchDecorator.setRadii(4, 1);
        clayPatchDecorator.setOverridableBlocks(Material.DIRT);
        gravelPatchDecorator.setAmount(1);
        gravelPatchDecorator.setRadii(6, 2);
        gravelPatchDecorator.setOverridableBlocks(Material.DIRT, Material.GRASS);

        doublePlantDecorator.setAmount(0);
        treeDecorator.setAmount(Integer.MIN_VALUE);
        treeDecorator.setTrees(TREES);
        desertWellDecorator.setAmount(0);
        flowerDecorator.setAmount(2);
        flowerDecorator.setFlowers(FLOWERS);
        tallGrassDecorator.setAmount(1);
        deadBushDecorator.setAmount(0);
        brownMushroomDecorator.setAmount(1);
        brownMushroomDecorator.setDensity(0.25D);
        redMushroomDecorator.setAmount(1);
        redMushroomDecorator.setDensity(0.125D);
        sugarCaneDecorator.setAmount(10);
        cactusDecorator.setAmount(0);
        flowingWaterDecorator.setAmount(50);
        flowingLavaDecorator.setAmount(20);

        EntityDecorator animalDecorator = new EntityDecorator(EntityType.COW, EntityType.SHEEP,
            EntityType.CHICKEN, EntityType.PIG);
        entityDecorators.add(animalDecorator);
    }

    public Collection<Biome> getBiomes() {
        return Collections.unmodifiableList(Arrays.asList(Biome.values()));
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        populateInGround(world, random, chunk);
        populateOnGround(world, random, chunk);
    }

    protected void populateInGround(World world, Random random, Chunk chunk) {
        for (BlockPopulator populator : inGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }

    protected void populateOnGround(World world, Random random, Chunk chunk) {
        boolean doMobSpawning = ((GlowWorld) world).getGameRuleMap().getBoolean(GameRules.DO_MOB_SPAWNING);
        if (doMobSpawning) {
            for (EntityDecorator entityDecorator : entityDecorators) {
                entityDecorator.populate(world, random, chunk);
            }
        }
        for (BlockPopulator populator : onGroundPopulators) {
            populator.populate(world, random, chunk);
        }
    }
}
