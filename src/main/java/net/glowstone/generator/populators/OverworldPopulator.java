package net.glowstone.generator.populators;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.glowstone.generator.populators.overworld.BiomePopulator;
import net.glowstone.generator.populators.overworld.BirchForestMountainsPopulator;
import net.glowstone.generator.populators.overworld.BirchForestPopulator;
import net.glowstone.generator.populators.overworld.DesertMountainsPopulator;
import net.glowstone.generator.populators.overworld.DesertPopulator;
import net.glowstone.generator.populators.overworld.ExtremeHillsPlusPopulator;
import net.glowstone.generator.populators.overworld.ExtremeHillsPopulator;
import net.glowstone.generator.populators.overworld.FlowerForestPopulator;
import net.glowstone.generator.populators.overworld.ForestPopulator;
import net.glowstone.generator.populators.overworld.IcePlainsPopulator;
import net.glowstone.generator.populators.overworld.IcePlainsSpikesPopulator;
import net.glowstone.generator.populators.overworld.JungleEdgePopulator;
import net.glowstone.generator.populators.overworld.JunglePopulator;
import net.glowstone.generator.populators.overworld.MegaSpruceTaigaPopulator;
import net.glowstone.generator.populators.overworld.MegaTaigaPopulator;
import net.glowstone.generator.populators.overworld.MesaForestPopulator;
import net.glowstone.generator.populators.overworld.MesaPopulator;
import net.glowstone.generator.populators.overworld.MushroomIslandPopulator;
import net.glowstone.generator.populators.overworld.OceanPopulator;
import net.glowstone.generator.populators.overworld.PlainsPopulator;
import net.glowstone.generator.populators.overworld.RoofedForestPopulator;
import net.glowstone.generator.populators.overworld.SavannaMountainsPopulator;
import net.glowstone.generator.populators.overworld.SavannaPopulator;
import net.glowstone.generator.populators.overworld.SunflowerPlainsPopulator;
import net.glowstone.generator.populators.overworld.SwamplandPopulator;
import net.glowstone.generator.populators.overworld.TaigaPopulator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

public class OverworldPopulator extends BlockPopulator {

    private final Map<Biome, BiomePopulator> biomePopulators = new HashMap<>();

    /**
     * Creates a populator with biome populators for all vanilla overworld biomes.
     */
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
