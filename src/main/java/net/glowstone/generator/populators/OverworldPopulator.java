package net.glowstone.generator.populators;

import net.glowstone.generator.populators.overworld.BadlandsPopulator;
import net.glowstone.generator.populators.overworld.BiomePopulator;
import net.glowstone.generator.populators.overworld.BirchForestPopulator;
import net.glowstone.generator.populators.overworld.DarkForestPopulator;
import net.glowstone.generator.populators.overworld.DesertMountainsPopulator;
import net.glowstone.generator.populators.overworld.DesertPopulator;
import net.glowstone.generator.populators.overworld.FlowerForestPopulator;
import net.glowstone.generator.populators.overworld.ForestPopulator;
import net.glowstone.generator.populators.overworld.GiantSpruceTaigaPopulator;
import net.glowstone.generator.populators.overworld.GiantTreeTaigaPopulator;
import net.glowstone.generator.populators.overworld.IceSpikesPopulator;
import net.glowstone.generator.populators.overworld.JungleEdgePopulator;
import net.glowstone.generator.populators.overworld.JunglePopulator;
import net.glowstone.generator.populators.overworld.MountainsPopulator;
import net.glowstone.generator.populators.overworld.MushroomFieldsPopulator;
import net.glowstone.generator.populators.overworld.OceanPopulator;
import net.glowstone.generator.populators.overworld.PlainsPopulator;
import net.glowstone.generator.populators.overworld.SavannaPopulator;
import net.glowstone.generator.populators.overworld.ShatteredSavannaPopulator;
import net.glowstone.generator.populators.overworld.SnowyTundraPopulator;
import net.glowstone.generator.populators.overworld.SunflowerPlainsPopulator;
import net.glowstone.generator.populators.overworld.SwampPopulator;
import net.glowstone.generator.populators.overworld.TaigaPopulator;
import net.glowstone.generator.populators.overworld.TallBirchForestPopulator;
import net.glowstone.generator.populators.overworld.WoodedBadlandsPopulator;
import net.glowstone.generator.populators.overworld.WoodedMountainsPopulator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class OverworldPopulator extends BlockPopulator {

    private final Map<Biome, BiomePopulator> biomePopulators = new EnumMap<>(Biome.class);

    /**
     * Creates a populator with biome populators for all vanilla overworld biomes.
     */
    public OverworldPopulator() {
        registerBiomePopulator(new BiomePopulator()); // defaults applied to all biomes
        registerBiomePopulator(new PlainsPopulator());
        registerBiomePopulator(new SunflowerPlainsPopulator());
        registerBiomePopulator(new ForestPopulator());
        registerBiomePopulator(new BirchForestPopulator());
        registerBiomePopulator(new TallBirchForestPopulator());
        registerBiomePopulator(new DarkForestPopulator());
        registerBiomePopulator(new FlowerForestPopulator());
        registerBiomePopulator(new DesertPopulator());
        registerBiomePopulator(new DesertMountainsPopulator());
        registerBiomePopulator(new JunglePopulator());
        registerBiomePopulator(new JungleEdgePopulator());
        registerBiomePopulator(new SwampPopulator());
        registerBiomePopulator(new TaigaPopulator());
        registerBiomePopulator(new GiantTreeTaigaPopulator());
        registerBiomePopulator(new GiantSpruceTaigaPopulator());
        registerBiomePopulator(new SnowyTundraPopulator());
        registerBiomePopulator(new IceSpikesPopulator());
        registerBiomePopulator(new SavannaPopulator());
        registerBiomePopulator(new ShatteredSavannaPopulator());
        registerBiomePopulator(new MountainsPopulator());
        registerBiomePopulator(new WoodedMountainsPopulator());
        registerBiomePopulator(new BadlandsPopulator());
        registerBiomePopulator(new WoodedBadlandsPopulator());
        registerBiomePopulator(new MushroomFieldsPopulator());
        registerBiomePopulator(new OceanPopulator());
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        Biome biome = world.getBiome((chunk.getX() << 4) + 8, (chunk.getZ() << 4) + 8);
        if (biomePopulators.containsKey(biome)) {
            biomePopulators.get(biome).populate(world, random, chunk);
        }
    }

    private void registerBiomePopulator(BiomePopulator populator) {
        for (Biome biome : populator.getBiomes()) {
            biomePopulators.put(biome, populator);
        }
    }
}
