package net.glowstone.generator.populators;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

/**
 * Populates the world with ores.
 */
public class OrePopulator extends BlockPopulator {

    private static final Map<OreVein, Integer> oreVeins = new LinkedHashMap<>();

    static {
        oreVeins.put(new OreVein(Material.DIRT, 0, 256, 32), 10);
        oreVeins.put(new OreVein(Material.GRAVEL, 0, 256, 32), 8);
        oreVeins.put(new OreVein(Material.STONE, 1, 0, 80, 32), 10);
        oreVeins.put(new OreVein(Material.STONE, 3, 0, 80, 32), 10);
        oreVeins.put(new OreVein(Material.STONE, 5, 0, 80, 32), 10);
        oreVeins.put(new OreVein(Material.COAL_ORE, 0, 128, 16), 20);
        oreVeins.put(new OreVein(Material.IRON_ORE, 0, 64, 8), 20);
        oreVeins.put(new OreVein(Material.GOLD_ORE, 0, 32, 8), 2);
        oreVeins.put(new OreVein(Material.REDSTONE_ORE, 0, 16, 7), 8);
        oreVeins.put(new OreVein(Material.DIAMOND_ORE, 0, 16, 7), 1);
        oreVeins.put(new OreVein(Material.LAPIS_ORE, 16, 16, 6), 1);
    }

    @Override
    public void populate(World world, Random random, Chunk source) {

        final int cx = (source.getX() << 4);
        final int cz = (source.getZ() << 4);

        for (Entry<OreVein, Integer> entry : oreVeins.entrySet()) {

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
                double dx1 = sourceX + 8 + Math.sin(angle) * (float) (amount / 8);
                double dx2 = sourceX + 8 - Math.sin(angle) * (float) (amount / 8);
                double dz1 = sourceZ + 8 + Math.cos(angle) * (float) (amount / 8);
                double dz2 = sourceZ + 8 - Math.cos(angle) * (float) (amount / 8);
                double dy1 = sourceY + random.nextInt(3) - 2;
                double dy2 = sourceY + random.nextInt(3) - 2;

                for (int i = 0; i < amount; i++) {
                    double originX = dx1 + (dx2 - dx1) * i / amount;
                    double originY = dy1 + (dy2 - dy1) * i / amount;
                    double originZ = dz1 + (dz2 - dz1) * i / amount;
                    double q = random.nextDouble() * amount / 16;
                    double hRadius = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2;
                    double vRadius = (Math.sin(i * (float) Math.PI / amount) + 1 * q + 1) / 2;

                    for (int x = (int) (originX - hRadius); x <= (int) (originY - vRadius); x++) {
                        double pX = (x + 0.5F - originX) / hRadius;
                        pX *= pX;
                        if (pX < 1) {
                            for (int y = (int) (originY - vRadius); y <= (int) (originY + vRadius); y++) {
                                double pY = (y + 0.5F - originY) / vRadius;
                                pY *= pY;
                                if (pX + pY < 1) {
                                    for (int z = (int) (originZ - hRadius); z <= (int) (originZ + hRadius); z++) {
                                        double pZ = (z + 0.5F - originZ) / hRadius;
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

        public OreVein(Material type, int minY, int maxY, int amount) {
            this(type, 0, minY, maxY, amount);
        }

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
