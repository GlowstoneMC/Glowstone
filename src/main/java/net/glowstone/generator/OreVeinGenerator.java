package net.glowstone.generator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class OreVeinGenerator {

    private final Map<OreVein, Integer> ores = new LinkedHashMap<>();

    public void addOre(int weight, Material type, int minY, int maxY, int amount) {
        addOre(weight, type, 0, minY, maxY, amount);
    }

    public void addOre(int weight, Material type, int data, int minY, int maxY, int amount) {
        ores.put(new OreVein(type, data, minY, maxY, amount), weight);
    }

    public void generate(World world, Random random, Chunk chunk) {

        final int cx = (chunk.getX() << 4);
        final int cz = (chunk.getZ() << 4);

        for (Entry<OreVein, Integer> entry : ores.entrySet()) {

            final OreVein vein = entry.getKey();
            for (int n = 0; n < entry.getValue(); n++) {

                int sourceX = cx + random.nextInt(16);
                int sourceZ = cz + random.nextInt(16);
                int sourceY = vein.getMinY() == vein.getMaxY() ?
                        random.nextInt(vein.getMinY()) + random.nextInt(vein.getMinY()) :
                            random.nextInt(vein.getMaxY() - vein.getMinY()) + vein.getMinY();

                final Material type = vein.getType();
                final int data = vein.getData();
                final int amount = vein.getAmount();

                float angle = random.nextFloat() * (float) Math.PI;
                double dx1 = sourceX + 8 + Math.sin(angle) * (float) (amount / 8.0F);
                double dx2 = sourceX + 8 - Math.sin(angle) * (float) (amount / 8.0F);
                double dz1 = sourceZ + 8 + Math.cos(angle) * (float) (amount / 8.0F);
                double dz2 = sourceZ + 8 - Math.cos(angle) * (float) (amount / 8.0F);
                double dy1 = sourceY + random.nextInt(3) - 2;
                double dy2 = sourceY + random.nextInt(3) - 2;

                for (int i = 0; i < amount; i++) {
                    double originX = dx1 + (dx2 - dx1) * i / amount;
                    double originY = dy1 + (dy2 - dy1) * i / amount;
                    double originZ = dz1 + (dz2 - dz1) * i / amount;
                    double q = random.nextDouble() * amount / 16.0D;
                    double hRadius = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2.0D;
                    double vRadius = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2.0D;

                    for (int x = (int) (originX - hRadius); x <= (int) (originX - hRadius); x++) {
                        double pX = (x + 0.5D - originX) / hRadius;
                        pX *= pX;
                        if (pX < 1) {
                            for (int y = (int) (originY - vRadius); y <= (int) (originY + vRadius); y++) {
                                double pY = (y + 0.5D - originY) / vRadius;
                                pY *= pY;
                                if (pX + pY < 1) {
                                    for (int z = (int) (originZ - hRadius); z <= (int) (originZ + hRadius); z++) {
                                        double pZ = (z + 0.5D - originZ) / hRadius;
                                        pZ *= pZ;
                                        if (pX + pY + pZ < 1 && world.getBlockAt(x, y, z).getType() == Material.STONE) {
                                            final Block block = world.getBlockAt(x, y, z);
                                            block.setType(type);
                                            block.setData((byte) data);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static class OreVein {

        private final Material type;
        private final int data;
        private final int minY;
        private final int maxY;
        private final int amount;

        public OreVein(Material type, int data, int minY, int maxY, int amount) {
            this.type = type;
            this.data = data;
            this.minY = minY;
            this.maxY = maxY;
            this.amount = ++amount;
        }

        public Material getType() {
            return type;
        }

        public int getData() {
            return data;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        public int getAmount() {
            return amount;
        }
    }
}
