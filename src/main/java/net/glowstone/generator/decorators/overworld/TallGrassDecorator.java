package net.glowstone.generator.decorators.overworld;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.TallGrass;
import org.bukkit.Chunk;
import org.bukkit.GrassSpecies;
import org.bukkit.World;
import org.bukkit.material.LongGrass;

import java.util.Random;

public class TallGrassDecorator extends BlockDecorator {

    private double fernDensity;

    public final void setFernDensity(double fernDensity) {
        this.fernDensity = fernDensity;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        int sourceX = (source.getX() << 4) + random.nextInt(16);
        int sourceZ = (source.getZ() << 4) + random.nextInt(16);
        int topBlock = world.getHighestBlockYAt(sourceX, sourceZ);
        if (topBlock == 0) {
            // Nothing to do if this column is empty
            return;
        }
        int sourceY = random.nextInt(Math.abs(topBlock << 1));

        // the grass species can change on each decoration pass
        GrassSpecies species = GrassSpecies.NORMAL;
        if (fernDensity > 0 && random.nextFloat() < fernDensity) {
            species = GrassSpecies.FERN_LIKE;
        }
        new TallGrass(new LongGrass(species)).generate(world, random, sourceX, sourceY, sourceZ);
    }
}
