package net.glowstone.generator.objects.trees;

import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class BigOakTree extends GenericTree {

    private final Random random = new Random();
    private static final float LEAF_DENSITY = 1.0F;
    private int maxLeafDistance = 5;
    private int trunkHeight;

    public BigOakTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        this.random.setSeed(random.nextLong());
        setHeight(this.random.nextInt(12) + 5);
    }

    public final void setMaxLeafDistance(int distance) {
        maxLeafDistance = distance;
    }

    @Override
    public boolean canPlace() {
        final Vector from = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        final Vector to = new Vector(loc.getBlockX(), loc.getBlockY() + height - 1, loc.getBlockZ());
        int blocks = countAvailableBlocks(from, to);
        if (blocks == -1) {
            return true;
        } else if (blocks > 5) {
            height = blocks;
            return true;
        }
        return false;
    }

    @Override
    public boolean generate() {
        if (!canPlaceOn() || !canPlace()) {
            return false;
        }

        trunkHeight = (int) ((double) height * 0.618D);
        if (trunkHeight >= height) {
            trunkHeight = height - 1;
        }

        final Collection<LeafNode> leafNodes = generateLeafNodes();

        // generate the leaves
        for (LeafNode node : leafNodes) {
            for (int y = 0; y < maxLeafDistance; y++) {
                float size = y > 0 && y < maxLeafDistance - 1.0F ? 3.0F : 2.0F;               
                int nodeDistance = (int) (0.618D + (double) size);
                for (int x = -nodeDistance; x <= nodeDistance; x++) {
                    for (int z = -nodeDistance; z <= nodeDistance; z++) {
                        double sizeX = (double) Math.abs(x) + 0.5D;
                        double sizeZ = (double) Math.abs(z) + 0.5D;
                        if (sizeX * sizeX + sizeZ * sizeZ <= (double) (size * size)) {
                            if (overridables.contains(delegate.getBlockState(loc.getWorld(), node.getX() + x, node.getY() + y, node.getZ() + z).getType())) {
                                delegate.setTypeAndRawData(loc.getWorld(), node.getX() + x, node.getY() + y, node.getZ() + z, Material.LEAVES, leavesType);
                            }
                        }
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < trunkHeight; y++) {
            delegate.setTypeAndRawData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ(), Material.LOG, logType);
        }

        // generate the branches
        for (LeafNode node : leafNodes) {
            if ((double) node.getBranchY() - loc.getBlockY() >= (double) height * 0.2D) {
                final Vector base = new Vector(loc.getBlockX(), node.getBranchY(), loc.getBlockZ());
                final Vector leafNode = new Vector(node.getX(), node.getY(), node.getZ());
                Vector branch = leafNode.subtract(base);
                int maxDistance = Math.max(Math.abs(branch.getBlockY()), Math.max(Math.abs(branch.getBlockX()), Math.abs(branch.getBlockZ())));
                float dX = (float) branch.getX() / (float) maxDistance;
                float dY = (float) branch.getY() / (float) maxDistance;
                float dZ = (float) branch.getZ() / (float) maxDistance;
                for (int i = 0; i <= maxDistance; i++) {
                    branch = base.clone().add(new Vector((double) (0.5F + (float) i * dX), (double) (0.5F + (float) i * dY), (double) (0.5F + (float) i * dZ)));
                    int x = Math.abs(branch.getBlockX() - base.getBlockX());
                    int z = Math.abs(branch.getBlockZ() - base.getBlockZ());
                    int max = Math.max(x, z);
                    int direction = max > 0 ? max == x ? 4 : 8 : 0; // EAST / SOUTH
                    delegate.setTypeAndRawData(loc.getWorld(), branch.getBlockX(), branch.getBlockY(), branch.getBlockZ(), Material.LOG, logType | direction);
                }
            }
        }

        return true;
    }

    private int countAvailableBlocks(Vector from, Vector to) {
        int n = 0;
        Vector target = to.subtract(from);
        int maxDistance = Math.max(Math.abs(target.getBlockY()), Math.max(Math.abs(target.getBlockX()), Math.abs(target.getBlockZ())));
        float dX = (float) target.getX() / (float) maxDistance;
        float dY = (float) target.getY() / (float) maxDistance;
        float dZ = (float) target.getZ() / (float) maxDistance;
        for (int i = 0; i <= maxDistance; i++, n++) {
            target = from.clone().add(new Vector((double) (0.5F + (float) i * dX), (double) (0.5F + (float) i * dY), (double) (0.5F + (float) i * dZ)));
            if (target.getBlockY() < 0 || target.getBlockY() > 255 ||
                    !overridables.contains(delegate.getBlockState(loc.getWorld(), target.getBlockX(), target.getBlockY(), target.getBlockZ()).getType())) {
                return n;
            }
        }
        return -1;
    }

    private Collection<LeafNode> generateLeafNodes() {
        final Collection<LeafNode> leafNodes = new ArrayList<>();
        int y = loc.getBlockY() + height - maxLeafDistance;
        int trunkTopY = loc.getBlockY() + trunkHeight;
        leafNodes.add(new LeafNode(loc.getBlockX(), y, loc.getBlockZ(), trunkTopY));

        int nodeCount = (int) (1.382D + Math.pow(LEAF_DENSITY * (double) height / 13.0D, 2.0D));
        nodeCount = nodeCount < 1 ? 1 : nodeCount;

        for (int l = --y - loc.getBlockY(); l >= 0; l--, y--) {
            float h = (float) height / 2.0F;
            float v = h - (float) l;
            float f = l < (float) height * 0.3D ? -1.0F :
               v == h ? h * 0.5F : h <= Math.abs(v) ? 0.0F : (float) Math.sqrt(h * h - v * v) * 0.5F;
            if (f >= 0.0F) {
                for (int i = 0; i < nodeCount; i++) {
                    double d1 = (double) f * ((double) random.nextFloat() + 0.328D);
                    double d2 = (double) random.nextFloat() * Math.PI * 2.0D;
                    int x = (int) (d1 * Math.sin(d2) + loc.getBlockX() + 0.5D);
                    int z = (int) (d1 * Math.cos(d2) + loc.getBlockZ() + 0.5D);
                    if (countAvailableBlocks(new Vector(x, y, z), new Vector(x, y + maxLeafDistance, z)) == -1) {
                        int offX = loc.getBlockX() - x;
                        int offZ = loc.getBlockZ() - z;
                        double distance = 0.381D * Math.sqrt(offX * offX + offZ * offZ);
                        int branchBaseY = Math.min(trunkTopY, (int) (y - distance));
                        if (countAvailableBlocks(new Vector(x, branchBaseY, z), new Vector(x, y, z)) == -1) {
                            leafNodes.add(new LeafNode(x, y, z, branchBaseY));
                        }
                    }
                }
            }
        }
        return leafNodes;
    }

    private static class LeafNode {
        private final int x;
        private final int y;
        private final int z;
        private final int branchY;

        public LeafNode(int x, int y, int z, int branchY) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.branchY = branchY;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getBranchY() {
            return branchY;
        }
    }
}
