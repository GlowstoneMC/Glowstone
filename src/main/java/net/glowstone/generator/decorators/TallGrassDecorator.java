package net.glowstone.generator.decorators;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.GrassSpecies;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.material.LongGrass;

public class TallGrassDecorator extends BlockDecorator {

    private final Map<Biome, Double> biomesFernDensity = new HashMap<Biome, Double>();

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
        while ((world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.AIR ||
                world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.LEAVES) &&
                sourceY > 0) {
            sourceY--;
        }

        // the grass species can change on each decoration pass
        GrassSpecies species = GrassSpecies.NORMAL;
        final Biome biome = world.getBiome(sourceX, sourceZ);
        if (biomesFernDensity.containsKey(biome)) {
            double density = biomesFernDensity.get(biome);
            if (random.nextFloat() < density) {
                species = GrassSpecies.FERN_LIKE;
            }
        }
        final LongGrass grass = new LongGrass(species);

        for (int i = 0; i < 128; i++) {
            int x = sourceX + random.nextInt(8) - random.nextInt(8);
            int z = sourceZ + random.nextInt(8) - random.nextInt(8);
            int y = sourceY + random.nextInt(4) - random.nextInt(4);

            if (y < 255 && world.getBlockAt(x, y, z).getType() == Material.AIR &&
                    (world.getBlockAt(x, y - 1, z).getType() == Material.GRASS ||
                    world.getBlockAt(x, y - 1, z).getType() == Material.DIRT)) {
                final BlockState state = world.getBlockAt(x, y, z).getState();
                state.setType(Material.LONG_GRASS);
                state.setData(grass);
                state.update(true);
            }
        }
    }
}
