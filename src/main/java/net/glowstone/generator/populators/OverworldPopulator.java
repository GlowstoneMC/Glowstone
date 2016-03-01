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
        registerBiomePopulator(new ForestPopulator());
        registerBiomePopulator(new BirchForestPopulator());
        registerBiomePopulator(new RoofedForestPopulator());
        registerBiomePopulator(new DesertPopulator());
        registerBiomePopulator(new JunglePopulator());
        registerBiomePopulator(new JungleEdgePopulator());
        registerBiomePopulator(new SwamplandPopulator());
        registerBiomePopulator(new TaigaPopulator());
        registerBiomePopulator(new IcePlainsPopulator());
        registerBiomePopulator(new SavannaPopulator());
        registerBiomePopulator(new ExtremeHillsPopulator());
        registerBiomePopulator(new MesaPopulator());
        registerBiomePopulator(new MushroomIslandPopulator());
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {
        final Biome biome = world.getBiome((chunk.getX() << 4) + 8, (chunk.getZ() << 4) + 8);
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
