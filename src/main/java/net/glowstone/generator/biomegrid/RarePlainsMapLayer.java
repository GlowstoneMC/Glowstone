package net.glowstone.generator.biomegrid;

public class RarePlainsMapLayer extends MapLayer {

    private final MapLayer belowLayer;

    public RarePlainsMapLayer(long seed, MapLayer belowLayer) {
        super(seed);
        this.belowLayer = belowLayer;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x - 1;
        int gridZ = z - 1;
        int gridSizeX = sizeX + 2;
        int gridSizeZ = sizeZ + 2;

        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                setCoordsSeed(x + j, z + i);
                finalValues[j + i * sizeX] = values[j + 1 + (i + 1) * gridSizeX];
            }
        }
        return finalValues;
    }
}
