package net.glowstone.generator.decorators.overworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.TallGrass;

import org.bukkit.Chunk;
import org.bukkit.GrassSpecies;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.material.LongGrass;

public class TallGrassDecorator extends BlockDecorator {

    private final Map<Biome, Double> biomesFernDensity = new HashMap<>();

    public final TallGrassDecorator setFernDensity(double fernDensity, Biome... biomes) {
        for (Biome biome : biomes) {
            biomesFernDensity.put(biome, fernDensity);
        }
        return this;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int sourceY = random.nextInt(world.getHighestBlockYAt(sourceX, sourceZ) << 1);

        // the grass species can change on each decoration pass
        GrassSpecies species = GrassSpecies.NORMAL;
        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesFernDensity.containsKey(biome)) {
            double density = biomesFernDensity.get(biome);
            if (random.nextFloat() < density) {
                species = GrassSpecies.FERN_LIKE;
            }
        }
        new TallGrass(new LongGrass(species)).generate(world, random, sourceX, sourceY, sourceZ);
    }
}
