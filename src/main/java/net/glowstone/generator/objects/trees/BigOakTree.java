package net.glowstone.generator.objects.trees;

import lombok.Data;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class BigOakTree extends GenericTree {

    private static final float LEAF_DENSITY = 1.0F;
    private int maxLeafDistance = 5;
    private int trunkHeight;

    public BigOakTree(Random random, BlockStateDelegate delegate) {
        super(random, delegate);
        setHeight(random.nextInt(12) + 5);
    }

    public final void setMaxLeafDistance(int distance) {
        maxLeafDistance = distance;
    }

    @Override
    public boolean canPlace(int baseX, int baseY, int baseZ, World world) {
        Vector from = new Vector(baseX, baseY, baseZ);
        Vector to = new Vector(baseX, baseY + height - 1, baseZ);
        int blocks = countAvailableBlocks(from, to, world);
        if (blocks == -1) {
            return true;
        } else if (blocks > 5) {
            height = blocks;
            return true;
        }
        return false;
    }

    @Override
    public boolean generate(World world, Random random, int blockX, int blockY, int blockZ) {
        if (!canPlaceOn(world.getBlockAt(blockX, blockY - 1, blockZ).getState())
                || !canPlace(blockX, blockY, blockZ, world)) {
            return false;
        }

        trunkHeight = (int) (height * 0.618D);
        if (trunkHeight >= height) {
            trunkHeight = height - 1;
        }

        Collection<LeafNode> leafNodes = generateLeafNodes(blockX, blockY, blockZ, world, random);

        // generate the leaves
        for (LeafNode node : leafNodes) {
            for (int y = 0; y < maxLeafDistance; y++) {
                float size = y > 0 && y < maxLeafDistance - 1.0F ? 3.0F : 2.0F;
                int nodeDistance = (int) (0.618D + size);
                for (int x = -nodeDistance; x <= nodeDistance; x++) {
                    for (int z = -nodeDistance; z <= nodeDistance; z++) {
                        double sizeX = Math.abs(x) + 0.5D;
                        double sizeZ = Math.abs(z) + 0.5D;
                        if (sizeX * sizeX + sizeZ * sizeZ <= size * size && overridables.contains(
                                blockTypeAt(node.getX() + x, node.getY() + y, node.getZ() + z,
                                        world))) {
                            delegate.setTypeAndRawData(world, node.getX() + x,
                                    node.getY() + y, node.getZ() + z, Material.LEAVES,
                                    leavesType);
                        }
                    }
                }
            }
        }

        // generate the trunk
        for (int y = 0; y < trunkHeight; y++) {
            delegate.setTypeAndRawData(world, blockX, blockY + y,
                    blockZ, Material.LOG, logType);
        }

        // generate the branches
        for (LeafNode node : leafNodes) {
            if ((double) node.getBranchY() - blockY >= height * 0.2D) {
                Vector base = new Vector(blockX, node.getBranchY(), blockZ);
                Vector leafNode = new Vector(node.getX(), node.getY(), node.getZ());
                Vector branch = leafNode.subtract(base);
                int maxDistance = Math.max(Math.abs(branch.getBlockY()),
                        Math.max(Math.abs(branch.getBlockX()), Math.abs(branch.getBlockZ())));
                float dx = (float) branch.getX() / maxDistance;
                float dy = (float) branch.getY() / maxDistance;
                float dz = (float) branch.getZ() / maxDistance;
                for (int i = 0; i <= maxDistance; i++) {
                    branch = base.clone().add(
                            new Vector(0.5 + i * dx, 0.5 + i * dy, 0.5 + i * dz));
                    int x = Math.abs(branch.getBlockX() - base.getBlockX());
                    int z = Math.abs(branch.getBlockZ() - base.getBlockZ());
                    int max = Math.max(x, z);
                    int direction = max > 0 ? max == x ? 4 : 8 : 0; // EAST / SOUTH
                    delegate.setTypeAndRawData(world,
                            branch.getBlockX(), branch.getBlockY(), branch.getBlockZ(),
                            Material.LOG, logType | direction);
                }
            }
        }

        return true;
    }

    private int countAvailableBlocks(Vector from, Vector to, World world) {
        int n = 0;
        Vector target = to.subtract(from);
        int maxDistance = Math.max(Math.abs(target.getBlockY()),
                Math.max(Math.abs(target.getBlockX()), Math.abs(target.getBlockZ())));
        float dx = (float) target.getX() / maxDistance;
        float dy = (float) target.getY() / maxDistance;
        float dz = (float) target.getZ() / maxDistance;
        for (int i = 0; i <= maxDistance; i++, n++) {
            target = from.clone()
                    .add(new Vector((double) (0.5F + i * dx), 0.5F + i * dy, 0.5F + i * dz));
            if (target.getBlockY() < 0 || target.getBlockY() > 255
                    || !overridables.contains(blockTypeAt(
                            target.getBlockX(), target.getBlockY(), target.getBlockZ(), world))) {
                return n;
            }
        }
        return -1;
    }

    private Collection<LeafNode> generateLeafNodes(int blockX, int blockY, int blockZ,
            World world, Random random) {
        Collection<LeafNode> leafNodes = new ArrayList<>();
        int y = blockY + height - maxLeafDistance;
        int trunkTopY = blockY + trunkHeight;
        leafNodes.add(new LeafNode(blockX, y, blockZ, trunkTopY));

        int nodeCount = (int) (1.382D + Math.pow(LEAF_DENSITY * (double) height / 13.0D, 2.0D));
        nodeCount = nodeCount < 1 ? 1 : nodeCount;

        for (int l = --y - blockY; l >= 0; l--, y--) {
            float h = height / 2.0F;
            float v = h - l;
            float f = l < (float) height * 0.3D ? -1.0F :
                    v == h ? h * 0.5F
                            : h <= Math.abs(v) ? 0.0F : (float) Math.sqrt(h * h - v * v) * 0.5F;
            if (f >= 0.0F) {
                for (int i = 0; i < nodeCount; i++) {
                    double d1 = f * (random.nextFloat() + 0.328D);
                    double d2 = random.nextFloat() * Math.PI * 2.0D;
                    int x = (int) (d1 * Math.sin(d2) + blockX + 0.5D);
                    int z = (int) (d1 * Math.cos(d2) + blockZ + 0.5D);
                    if (countAvailableBlocks(new Vector(x, y, z),
                            new Vector(x, y + maxLeafDistance, z), world) == -1) {
                        int offX = blockX - x;
                        int offZ = blockZ - z;
                        double distance = 0.381D * Math.hypot(offX, offZ);
                        int branchBaseY = Math.min(trunkTopY, (int) (y - distance));
                        if (countAvailableBlocks(
                                new Vector(x, branchBaseY, z), new Vector(x, y, z), world) == -1) {
                            leafNodes.add(new LeafNode(x, y, z, branchBaseY));
                        }
                    }
                }
            }
        }
        return leafNodes;
    }

    @Data
    private static final class LeafNode {
        private final int x;
        private final int y;
        private final int z;
        private final int branchY;
    }
}
