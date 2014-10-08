package net.glowstone.generator;

import net.glowstone.generator.populators.FlowerPopulator;
import net.glowstone.generator.populators.SnowPopulator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

/**
 * A 'cake town' generator as a temporary Skylands.
 */
public class CakeTownGenerator extends GlowChunkGenerator {

    public CakeTownGenerator() {
        super(
                // In-ground
                //new LakePopulator(),
                // On-ground
                //new DesertPopulator(), new TreePopulator(), new MushroomPopulator(),
                new SnowPopulator(), new FlowerPopulator() // Belowground
                //new CavePopulator()
        );
    }

    @Override
    public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
        final int base = WORLD_DEPTH / 4;
        final int top = WORLD_DEPTH / 2 + base;

        Location center = new Location(world, 0, base, 0);

        byte[] buf = start(Material.AIR);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = base; y <= top; y++) {
                    int realX = chunkX * 16 + x;
                    int realZ = chunkZ * 16 + z;
                    double dist = new Location(world, realX, y, realZ).distance(center);
                    if (dist < 100) {
                        if (y <= base + 1 && y >= base) {
                            set(buf, x, y, z, Material.BEDROCK);
                        } else if (y == top) {
                            set(buf, x, y, z, Material.GRASS);
                        } else {
                            set(buf, x, y, z, Material.STONE);
                        }
                    } else if (dist < 102) {
                        if (y <= base + 1 && y >= base) {
                            set(buf, x, y, z, Material.BEDROCK);
                        } else {
                            set(buf, x, y, z, Material.GRASS);
                        }
                    }
                }
            }
        }

        return buf;
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        int x = random.nextInt(WORLD_DEPTH) - WORLD_DEPTH / 2;
        int z = random.nextInt(WORLD_DEPTH) - WORLD_DEPTH / 2;
        return new Location(world, x, world.getHighestBlockYAt(x, z), z);
    }

}
