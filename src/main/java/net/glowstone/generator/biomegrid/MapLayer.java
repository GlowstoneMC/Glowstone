package net.glowstone.generator.biomegrid;

import net.glowstone.generator.biomegrid.WhittakerMapLayer.ClimateType;
import net.glowstone.generator.biomegrid.ZoomMapLayer.ZoomType;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;

import java.util.Random;

public abstract class MapLayer {

    private final Random random = new Random();
    private long seed;

    public MapLayer(long seed) {
        this.seed = seed;
    }

    /**
     * Creates the instances for the given map.
     * @param seed the world seed
     * @param environment the type of dimension
     * @param worldType the world generator
     * @return an array of all map layers this dimension needs
     */
    public static MapLayer[] initialize(long seed, Environment environment, WorldType worldType) {
        if (environment == Environment.NORMAL && worldType == WorldType.FLAT) {
            return new MapLayer[]{new ConstantBiomeMapLayer(seed, Biome.PLAINS), null};
        } else if (environment == Environment.NETHER) {
            return new MapLayer[]{new ConstantBiomeMapLayer(seed, Biome.HELL), null};
        } else if (environment == Environment.THE_END) {
            return new MapLayer[]{new ConstantBiomeMapLayer(seed, Biome.SKY), null};
        }

        int zoom = 2;
        if (worldType == WorldType.LARGE_BIOMES) {
            zoom = 4;
        }

        MapLayer layer = new NoiseMapLayer(seed); // this is initial land spread layer
        layer = new WhittakerMapLayer(seed + 1, layer, ClimateType.WARM_WET);
        layer = new WhittakerMapLayer(seed + 1, layer, ClimateType.COLD_DRY);
        layer = new WhittakerMapLayer(seed + 2, layer, ClimateType.LARGER_BIOMES);
        for (int i = 0; i < 2; i++) {
            layer = new ZoomMapLayer(seed + 100 + i, layer, ZoomType.BLURRY);
        }
        for (int i = 0; i < 2; i++) {
            layer = new ErosionMapLayer(seed + 3 + i, layer);
        }
        layer = new DeepOceanMapLayer(seed + 4, layer);

        MapLayer layerMountains = new BiomeVariationMapLayer(seed + 200, layer);
        for (int i = 0; i < 2; i++) {
            layerMountains = new ZoomMapLayer(seed + 200 + i, layerMountains);
        }

        layer = new BiomeMapLayer(seed + 5, layer);
        for (int i = 0; i < 2; i++) {
            layer = new ZoomMapLayer(seed + 200 + i, layer);
        }
        layer = new BiomeEdgeMapLayer(seed + 200, layer);
        layer = new BiomeVariationMapLayer(seed + 200, layer, layerMountains);
        layer = new RarePlainsMapLayer(seed + 201, layer);
        layer = new ZoomMapLayer(seed + 300, layer);
        layer = new ErosionMapLayer(seed + 6, layer);
        layer = new ZoomMapLayer(seed + 400, layer);
        layer = new BiomeThinEdgeMapLayer(seed + 400, layer);
        layer = new ShoreMapLayer(seed + 7, layer);
        for (int i = 0; i < zoom; i++) {
            layer = new ZoomMapLayer(seed + 500 + i, layer);
        }

        MapLayer layerRiver = layerMountains;
        layerRiver = new ZoomMapLayer(seed + 300, layerRiver);
        layerRiver = new ZoomMapLayer(seed + 400, layerRiver);
        for (int i = 0; i < zoom; i++) {
            layerRiver = new ZoomMapLayer(seed + 500 + i, layerRiver);
        }
        layerRiver = new RiverMapLayer(seed + 10, layerRiver);
        layer = new RiverMapLayer(seed + 1000, layerRiver, layer);

        MapLayer layerLowerRes = layer;
        for (int i = 0; i < 2; i++) {
            layer = new ZoomMapLayer(seed + 2000 + i, layer);
        }

        layer = new SmoothMapLayer(seed + 1001, layer);

        return new MapLayer[]{layer, layerLowerRes};
    }

    public void setCoordsSeed(int x, int z) {
        random.setSeed(seed);
        random.setSeed(x * random.nextLong() + z * random.nextLong() ^ seed);
    }

    public int nextInt(int max) {
        return random.nextInt(max);
    }

    public abstract int[] generateValues(int x, int z, int sizeX, int sizeZ);
}
