package net.glowstone.generator.biomegrid;

public class SmoothMapLayer extends MapLayer {

    private final MapLayer belowLayer;

    public SmoothMapLayer(long seed, MapLayer belowLayer) {
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
                // This applies smoothing using Von Neumann neighborhood
                // it takes a 3x3 grid with a cross shape and analyzes values as follow
                // 0X0
                // XxX
                // 0X0
                // it is required that we use the same shape that was used for what we
                // want to smooth
                int upperVal = values[j + 1 + i * gridSizeX];
                int lowerVal = values[j + 1 + (i + 2) * gridSizeX];
                int leftVal = values[j + (i + 1) * gridSizeX];
                int rightVal = values[j + 2 + (i + 1) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];
                if (upperVal == lowerVal && leftVal == rightVal) {
                    setCoordsSeed(x + j, z + i);
                    centerVal = nextInt(2) == 0 ? upperVal : leftVal;
                } else if (upperVal == lowerVal) {
                    centerVal = upperVal;
                } else if (leftVal == rightVal) {
                    centerVal = leftVal;
                }
                finalValues[j + i * sizeX] = centerVal;
            }
        }
        return finalValues;
    }
}
