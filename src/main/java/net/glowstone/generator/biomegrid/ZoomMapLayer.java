package net.glowstone.generator.biomegrid;

public class ZoomMapLayer extends MapLayer {

    private final MapLayer belowLayer;
    private final ZoomType zoomType;

    public ZoomMapLayer(long seed, MapLayer belowLayer) {
        this(seed, belowLayer, ZoomType.NORMAL);
    }

    /**
     * Creates a map layer. TODO: improve documentation
     *
     * @param seed the layer random seed
     * @param belowLayer the layer generated before this one
     * @param zoomType the zoom-type parameter
     */
    public ZoomMapLayer(long seed, MapLayer belowLayer, ZoomType zoomType) {
        super(seed);
        this.belowLayer = belowLayer;
        this.zoomType = zoomType;
    }

    @Override
    public int[] generateValues(int x, int z, int sizeX, int sizeZ) {
        int gridX = x >> 1;
        int gridZ = z >> 1;
        int gridSizeX = (sizeX >> 1) + 2;
        int gridSizeZ = (sizeZ >> 1) + 2;
        int[] values = belowLayer.generateValues(gridX, gridZ, gridSizeX, gridSizeZ);

        int zoomSizeX = gridSizeX - 1 << 1;
        int zoomSizeZ = gridSizeZ - 1 << 1;
        int[] tmpValues = new int[zoomSizeX * zoomSizeZ];
        for (int i = 0; i < gridSizeZ - 1; i++) {
            int n = i * 2 * zoomSizeX;
            int upperLeftVal = values[i * gridSizeX];
            int lowerLeftVal = values[(i + 1) * gridSizeX];
            for (int j = 0; j < gridSizeX - 1; j++) {
                setCoordsSeed(gridX + j << 1, gridZ + i << 1);
                tmpValues[n] = upperLeftVal;
                tmpValues[n + zoomSizeX] = nextInt(2) > 0 ? upperLeftVal : lowerLeftVal;
                int upperRightVal = values[j + 1 + i * gridSizeX];
                int lowerRightVal = values[j + 1 + (i + 1) * gridSizeX];
                tmpValues[n + 1] = nextInt(2) > 0 ? upperLeftVal : upperRightVal;
                tmpValues[n + 1 + zoomSizeX] = getNearest(upperLeftVal, upperRightVal, lowerLeftVal,
                    lowerRightVal);
                upperLeftVal = upperRightVal;
                lowerLeftVal = lowerRightVal;
                n += 2;
            }
        }
        int[] finalValues = new int[sizeX * sizeZ];
        for (int i = 0; i < sizeZ; i++) {
            for (int j = 0; j < sizeX; j++) {
                finalValues[j + i * sizeX] = tmpValues[j + (i + (z & 1)) * zoomSizeX + (x & 1)];
            }
        }

        return finalValues;
    }

    private int getNearest(int upperLeftVal, int upperRightVal, int lowerLeftVal,
        int lowerRightVal) {
        if (zoomType == ZoomType.NORMAL) {
            if (upperRightVal == lowerLeftVal && lowerLeftVal == lowerRightVal) {
                return upperRightVal;
            } else if (upperLeftVal == upperRightVal && upperLeftVal == lowerLeftVal) {
                return upperLeftVal;
            } else if (upperLeftVal == upperRightVal && upperLeftVal == lowerRightVal) {
                return upperLeftVal;
            } else if (upperLeftVal == lowerLeftVal && upperLeftVal == lowerRightVal) {
                return upperLeftVal;
            } else if (upperLeftVal == upperRightVal && lowerLeftVal != lowerRightVal) {
                return upperLeftVal;
            } else if (upperLeftVal == lowerLeftVal && upperRightVal != lowerRightVal) {
                return upperLeftVal;
            } else if (upperLeftVal == lowerRightVal && upperRightVal != lowerLeftVal) {
                return upperLeftVal;
            } else if (upperRightVal == lowerLeftVal && upperLeftVal != lowerRightVal) {
                return upperRightVal;
            } else if (upperRightVal == lowerRightVal && upperLeftVal != lowerLeftVal) {
                return upperRightVal;
            } else if (lowerLeftVal == lowerRightVal && upperLeftVal != upperRightVal) {
                return lowerLeftVal;
            }
        }
        int[] values = new int[]{upperLeftVal, upperRightVal, lowerLeftVal, lowerRightVal};
        return values[nextInt(values.length)];
    }

    public enum ZoomType {
        NORMAL,
        BLURRY
    }
}
