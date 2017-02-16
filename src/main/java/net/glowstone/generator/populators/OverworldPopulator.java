package net.glowstone.generator.populators;

import net.glowstone.generator.populators.overworld.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OverworldPopulator extends BlockPopulator {

    private final Map<Biome, BiomePopulator> biomePopulators = new HashMap<>();

    public OverworldPopulator() {
        registerBiomePopulator(new BiomePopulator()); // defaults applied to all biomes
        registerBiomePopulator(new PlainsPopulator());
        registerBiomePopulator(new SunflowerPlainsPopulator());
        registerBiomePopulator(new ForestPopulator());
        registerBiomePopulator(new BirchForestPopulator());
        registerBiomePopulator(new BirchForestMountainsPopulator());
        registerBiomePopulator(new RoofedForestPopulator());
        registerBiomePopulator(new FlowerForestPopulator());
        registerBiomePopulator(new DesertPopulator());
        registerBiomePopulator(new DesertMountainsPopulator());
        registerBiomePopulator(new JunglePopulator());
        registerBiomePopulator(new JungleEdgePopulator());
        registerBiomePopulator(new SwamplandPopulator());
        registerBiomePopulator(new TaigaPopulator());
        registerBiomePopulator(new MegaTaigaPopulator());
        registerBiomePopulator(new MegaSpruceTaigaPopulator());
        registerBiomePopulator(new IcePlainsPopulator());
        registerBiomePopulator(new IcePlainsSpikesPopulator());
        registerBiomePopulator(new SavannaPopulator());
        registerBiomePopulator(new SavannaMountainsPopulator());
        registerBiomePopulator(new ExtremeHillsPopulator());
        registerBiomePopulator(new ExtremeHillsPlusPopulator());
        registerBiomePopulator(new MesaPopulator());
        registerBiomePopulator(new MesaForestPopulator());
        registerBiomePopulator(new MushroomIslandPopulator());
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
