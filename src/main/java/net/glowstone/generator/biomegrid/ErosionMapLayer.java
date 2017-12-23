package net.glowstone.generator.biomegrid;

public class ErosionMapLayer extends MapLayer {

    private final MapLayer belowLayer;

    public ErosionMapLayer(long seed, MapLayer belowLayer) {
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
                // This applies erosion using Rotated Von Neumann neighborhood
                // it takes a 3x3 grid with a cross shape and analyzes values as follow
                // X0X
                // 0X0
                // X0X
                // the grid center value decides how we are proceeding:
                // - if it's land and it's surrounded by at least 1 ocean cell there are 4/5 chances
                // to proceed to land weathering, and 1/5 chance to spread some land.
                // - if it's ocean and it's surrounded by at least 1 land cell, there are 2/3
                // chances to proceed to land weathering, and 1/3 chance to spread some land.
                int upperLeftVal = values[j + i * gridSizeX];
                int lowerLeftVal = values[j + (i + 2) * gridSizeX];
                int upperRightVal = values[j + 2 + i * gridSizeX];
                int lowerRightVal = values[j + 2 + (i + 2) * gridSizeX];
                int centerVal = values[j + 1 + (i + 1) * gridSizeX];

                setCoordsSeed(x + j, z + i);
                if (centerVal != 0 && (upperLeftVal == 0 || upperRightVal == 0 || lowerLeftVal == 0
                    || lowerRightVal == 0)) {
                    finalValues[j + i * sizeX] = nextInt(5) == 0 ? 0 : centerVal;
                } else if (centerVal == 0 && (upperLeftVal != 0 || upperRightVal != 0
                    || lowerLeftVal != 0 || lowerRightVal != 0)) {
                    if (nextInt(3) == 0) {
                        finalValues[j + i * sizeX] = upperLeftVal;
                    } else {
                        finalValues[j + i * sizeX] = 0;
                    }
                } else {
                    finalValues[j + i * sizeX] = centerVal;
                }
            }
        }
        return finalValues;
    }
}
