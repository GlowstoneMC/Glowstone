package net.glowstone.generator.decorators.overworld;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;

import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.generator.objects.Lake;

public class LakeDecorator extends BlockDecorator {

    private final Material type;

    public LakeDecorator(Material type) {
        if (type != Material.STATIONARY_WATER && type != Material.STATIONARY_LAVA) {
            throw new IllegalArgumentException("Lake material must be STATIONARY_WATER or STATIONARY_LAVA");
        }
        this.type = type;
    }

    @Override
    public void decorate(World world, Random random, Chunk source) {
        if (random.nextInt(type == Material.STATIONARY_WATER ? 4 : 8) == 0) {
            int sourceX = (source.getX() << 4) + random.nextInt(16);
            int sourceZ = (source.getZ() << 4) + random.nextInt(16);
            int sourceY = random.nextInt(type == Material.STATIONARY_WATER ? 256 : random.nextInt(248) + 8);
            while (world.getBlockAt(sourceX, sourceY, sourceZ).getType() == Material.AIR && sourceY > 5) {
                sourceY--;
            }
            if (sourceY < 5) {
                return;
            }
            new Lake(type).generate(world, random, sourceX, sourceY, sourceZ);
        }
    }
}
