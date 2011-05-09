package net.glowstone.world;

import java.util.ArrayList;
import java.util.Map.Entry;
import net.glowstone.GlowChunk;
import org.bukkit.Material;

/**
 * A slightly more complex {@link WorldGenerator} using the InfiniMap algorithm.
 * @author Tad
 */
public class InfiniMapWorldGenerator implements WorldGenerator {
    
    /**
     * Class representing a biome type.
     */
    abstract private class Biome {
        
        public int weight;
        public int midheight;
        public int[] noise;
        
        protected Biome(int w, int mid, int n1, int n2, int n3, int n4) {
            weight = w; midheight = mid;
            noise = new int[] { n1, n2, n3, n4 };
        }
        
        abstract public Material get(int y, int h);

        // Block mappers

        protected Material forestBlock(int y, int h) {
            if (y > h) return Material.AIR;
            else if (y == h) return Material.GRASS;
            else if (y > h - 5) return Material.DIRT;
            else if (y <= 1) return Material.BEDROCK;
            else return Material.STONE;
        }

        protected Material desertBlock(int y, int h) {
            if (y > h) return Material.AIR;
            else if (y > h - 5) return Material.SAND;
            else if (y <= 1) return Material.BEDROCK;
            else return Material.STONE;
        }

        protected Material costalBlock(int y, int h) {
            if (h >= 64) return forestBlock(y, h);
            else if (y > 64) return Material.AIR;
            else if (y > h) return Material.WATER;
            else if (y <= 1) return Material.BEDROCK;
            else return Material.STONE;
        }

        protected Material floatingBlock(int y, int h) {
            int h2 = 128 - h + 32;
            int base = 128 - 16;
            int h3 = base - 3 * (h2 - base);
            if (y > h2) return Material.AIR;
            else if (y == h2 && y > h3) return Material.GRASS;
            else if (y == h2 && y == h3) return Material.SAND;
            else if (y >= h2 - 8 && y >= h3) return Material.DIRT;
            else if (y >= h3) return Material.STONE;
            else if (y > 64) return Material.AIR;
            else if (y > h) return Material.WATER;
            else if (y >= h - 5) return Material.GRAVEL;
            else if (y > 1) return Material.STONE;
            else return Material.BEDROCK;
        }
    }

    // Biomes

    private class Forest extends Biome {
        public Forest() { super(100, 68, 16, 1, 4, 2); }
        public Material get(int y, int h) { return costalBlock(y, h); }
    }

    private class Islands extends Biome {
        public Islands() { super(50, 62, 16, 1, 2, 4); }
        public Material get(int y, int h) { return costalBlock(y, h); }
    }

    private class Floating extends Biome {
        public Floating() { super(10, 32, 16, 1, 2, 1); }
        public Material get(int y, int h) { return floatingBlock(y, h); }
    }

    private class Desert extends Biome {
        public Desert() { super(50, 74, 16, 1, 2, 4); }
        public Material get(int y, int h) { return desertBlock(y, h); }
    }

    private class Mountains extends Biome {
        public Mountains() { super(30, 80, 16, 2, 16, 1); }
        public Material get(int y, int h) { return costalBlock(y, h); }
    }
    
    private ArrayList<Biome> biomes;
    private Perlin2D biomeGen;
    private Perlin2D[] perlins;
    
    public InfiniMapWorldGenerator() {
        biomes = new ArrayList<Biome>();
        
        biomes.add(new Forest());
        biomes.add(new Islands());
        biomes.add(new Floating());
        biomes.add(new Desert());
        biomes.add(new Mountains());

        int total = 0;
        for (Biome b : biomes) {
            total += b.weight;
        }
        biomeGen = new Perlin2D(total, 0.5, new int[] { 31, 15731, 789221, 1376312589 });
        perlins = new Perlin2D[] {
            new Perlin2D(16, 1, new int[] { 57, 60493, 19990303, 1376312589 }),
            new Perlin2D(16, 1, new int[] { 29, 15731, 789221, 1376312589 })
        };
    }

    private Biome biomeAt(double noiseX, double noiseY) {
        double bNoise = biomeGen.amplitude/2 + biomeGen.noise(noiseX, noiseY);
        int total = 0;
        for (Biome b : biomes) {
            total += b.weight;
            if (bNoise < total) return b;
        }
        return biomes.get(0);
    }

    private int heightAt(double noiseX, double noiseY) {
        Biome biome = biomeAt(noiseX, noiseY);
        double noise = biome.midheight;
        for (int i = 0; i < 2; ++i) {
            Perlin2D perlin = perlins[i];
            perlin.amplitude = biome.noise[2*i];
            perlin.frequency = biome.noise[2*i + 1];
            noise += perlin.noise(noiseX, noiseY);
        }
        return (int) noise;
    }

    private void nearestOtherBiome(double noiseX, double noiseY, Double newX, Double newY) {
        newX = noiseX; newY = noiseY;
        double d = 1.0 / 16.0;
        Biome biome = biomeAt(noiseX, noiseY);
        for (int i = 0; i < 5; ++i) {
            if (biomeAt(noiseX - i*d, noiseY) != biome) {
                newX = noiseX - i*d; return;
            } else if (biomeAt(noiseX + i*d, noiseY) != biome) {
                newX = noiseX + i*d; return;
            } else if (biomeAt(noiseX, noiseY - i*d) != biome) {
                newY = noiseY - i*d; return;
            } else if (biomeAt(noiseX, noiseY + i*d) != biome) {
                newY = noiseY + i*d; return;
            }
        }
    }

    public GlowChunk generate(int chunkX, int chunkZ) {
		GlowChunk chunk = new GlowChunk(chunkX, chunkZ);
		for (int x = 0; x < GlowChunk.WIDTH; x++) {
			for (int z = 0; z < GlowChunk.HEIGHT; z++) {
                double noiseX = chunkX + (x / 16.0);
                double noiseY = chunkZ + (z / 16.0);
                Biome biome = biomeAt(noiseX, noiseY);
                int height = heightAt(noiseX, noiseY);

                Double nearestX = 0.0, nearestY = 0.0;
                nearestOtherBiome(noiseX, noiseY, nearestX, nearestY);

                if (biomeAt(nearestX, nearestY) != biome) {
                    double dx = Math.abs(nearestX - noiseX);
                    double dy = Math.abs(nearestY - noiseY);
                    double bdist = 16 * Math.max(dx, dy);
                    double f = (bdist + 1.0) / (bdist + 2.0);
                    height = (int)(f * height + (1.0 - f) * heightAt(nearestX, nearestY));
                }

				for (int y = 0; y < GlowChunk.DEPTH; y++) {
					chunk.setType(x, z, y, biome.get(y, height).getId());
					chunk.setMetaData(x, z, y, 0);
					chunk.setBlockLight(x, z, y, 0);
					chunk.setSkyLight(x, z, y, 15);
				}
			}
		}
		return chunk;
	}
    
}
