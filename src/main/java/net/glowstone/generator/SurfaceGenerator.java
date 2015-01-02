package net.glowstone.generator;

import net.glowstone.generator.populators.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Map;
import java.util.Random;

/**
 * Basic generator with lots of hills.
 */
public class SurfaceGenerator extends GlowChunkGenerator {

    public SurfaceGenerator() {
        super(
                // In-ground
                new LakePopulator(),
                // On-ground
                // Desert is before tree and mushroom but snow is after so trees have snow on top
                new DesertPopulator(),
                new TreePopulator(),
                new MushroomPopulator(),
                new SnowPopulator(),
                new FlowerPopulator(),
                new BiomePopulator(),
                // Below-ground
                new DungeonPopulator(),
                //new CavePopulator(),
                new OrePopulator()
        );
    }

    @Override
    public byte[] generate(World world, Random random, int chunkX, int chunkZ) {
        Map<String, OctaveGenerator> octaves = getWorldOctaves(world);
        OctaveGenerator noiseHeight = octaves.get("height");
        OctaveGenerator noiseJitter = octaves.get("jitter");
        OctaveGenerator noiseType = octaves.get("type");

        chunkX <<= 4;
        chunkZ <<= 4;

        boolean nether = world.getEnvironment() == Environment.NETHER;
        Material matMain = nether ? Material.NETHERRACK : Material.DIRT;
        Material matShore = nether ? Material.SOUL_SAND : Material.SAND;
        Material matShore2 = Material.GRAVEL;
        Material matTop = nether ? Material.NETHERRACK : Material.GRASS;
        Material matUnder = nether ? Material.NETHERRACK : Material.STONE;
        Material matLiquid = nether ? Material.STATIONARY_LAVA : Material.STATIONARY_WATER;

        byte[] buf = start(Material.AIR);

        int baseHeight = WORLD_DEPTH / 2;
        double terrainHeight = 50;
        boolean noDirt = true;
        int waterLevel = WORLD_DEPTH / 2;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int deep = 0;
                for (int y = (int) Math.min(baseHeight
                        + noiseHeight.noise(x + chunkX, z + chunkZ, 0.7, 0.6, true)
                        * terrainHeight
                        + noiseJitter.noise(x + chunkX, z + chunkZ, 0.5, 0.5)
                        * 1.5, WORLD_DEPTH - 1); y > 0; y--) {
                    double terrainType = noiseType.noise(x + chunkX, y, z + chunkZ, 0.5, 0.5);
                    Material ground = matTop;
                    if (Math.abs(terrainType) < random.nextDouble() / 3 && !noDirt) {
                        ground = matMain;
                    } else if (deep != 0 || y < waterLevel) {
                        ground = matMain;
                    }

                    if (Math.abs(y - waterLevel) < 5 - random.nextInt(2) && deep < 7) {
                        if (terrainType < random.nextDouble() / 2) {
                            if (terrainType < random.nextDouble() / 4) {
                                ground = matShore;
                            } else {
                                ground = matShore2;
                            }
                        }
                    }

                    if (deep > random.nextInt(3) + 6) {
                        ground = matUnder;
                    }

                    set(buf, x, y, z, ground);
                    deep++;
                }
                set(buf, x, 0, z, Material.BEDROCK);
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < waterLevel; y++) {
                    if (get(buf, x, y, z) == Material.AIR) {
                        set(buf, x, y, z, matLiquid);
                    }
                }
            }
        }

        return buf;
    }

    @Override
    protected void createWorldOctaves(World world, Map<String, OctaveGenerator> octaves) {
        Random seed = new Random(world.getSeed());

        /* With default settings, this is 5 octaves. With tscale=256,terrainheight=50,
         * this comes out to 14 octaves, which makes more complex terrain at the cost
         * of more complex generation. Without this, the terrain looks bad, especially
         * on higher tscale/terrainheight pairs. */
        double value = Math.round(Math.sqrt(50 * 256.0 / (128 - 50)) * 1.1 - 0.2);
        OctaveGenerator gen = new SimplexOctaveGenerator(seed, Math.max((int) value, 5));
        gen.setScale(1 / 256.0);
        octaves.put("height", gen);

        gen = new SimplexOctaveGenerator(seed, gen.getOctaves().length / 2);
        gen.setScale(Math.min(256.0 / 1024, 1 / 32.0));
        octaves.put("jitter", gen);

        gen = new SimplexOctaveGenerator(seed, 2);
        gen.setScale(1 / WORLD_DEPTH);
        octaves.put("type", gen);
    }

    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 2 + world.getHighestBlockYAt(0, 0), 0);
    }

}
