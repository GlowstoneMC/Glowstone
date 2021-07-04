package net.glowstone.generator.decorators.overworld;

import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.generator.decorators.BlockDecorator;
import net.glowstone.util.noise.PerlinOctaveGenerator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SurfaceCaveDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk c) {
        if (random.nextInt(8) != 0) {
            return;
        }
        GlowChunk chunk = (GlowChunk) c;
        final int startCx = random.nextInt(16);
        final int startCz = random.nextInt(16);
        final int startY = chunk.getHeight(startCx, startCz);
        final GlowBlock startBlock = chunk.getBlock(startCx, startY, startCz);
        if (startY > 128) {
            return;
        }
        PerlinOctaveGenerator octaves = new PerlinOctaveGenerator(random, 3, 4, 2, 4);
        int cx = c.getX() << 4;
        int cz = c.getZ() << 4;
        double[] noise = octaves.getFractalBrownianMotion(cx, cz, 0, 0.5D, 0.2D);
        double[] angles = new double[noise.length];
        for (int i = 0; i < noise.length; i++) {
            angles[i] = 360.0 * noise[i];
        }
        int sectionCount = angles.length / 2;
        List<BlockVector> nodes = new ArrayList<>();
        BlockVector currentNode = new BlockVector(startBlock.getX(), startBlock.getY(),
            startBlock.getZ());
        nodes.add(currentNode.clone());
        int length = 5;
        for (int i = 0; i < sectionCount; i++) {
            double yaw = angles[i + sectionCount];
            int deltaY = -Math.abs(NumberConversions.floor(noise[i] * length));
            int deltaX = NumberConversions.floor((double) length * Math.cos(Math.toRadians(yaw)));
            int deltaZ = NumberConversions.floor((double) length * Math.sin(Math.toRadians(yaw)));
            currentNode.add(new Vector(deltaX, deltaY, deltaZ));
            nodes.add(new BlockVector(currentNode.getBlockX(), currentNode.getBlockY(),
                currentNode.getBlockZ()));
        }
        for (BlockVector node : nodes) {
            if (node.getBlockY() < 4) {
                continue;
            }
            GlowBlock block = (GlowBlock) world
                .getBlockAt(node.getBlockX(), node.getBlockY(), node.getBlockZ());
            caveAroundRay(block, random);
        }
    }

    private void caveAroundRay(GlowBlock block, Random random) {
        int radius = random.nextInt(2) + 2;
        final int blockX = block.getX();
        final int blockY = block.getY();
        final int blockZ = block.getZ();
        for (int x = blockX - radius; x <= blockX + radius; x++) {
            for (int y = blockY - radius; y <= blockY + radius; y++) {
                for (int z = blockZ - radius; z <= blockZ + radius; z++) {
                    double distanceSquared =
                        (blockX - x) * (blockX - x) + (blockY - y) * (blockY - y)
                                + (blockZ - z) * (blockZ - z);
                    if (distanceSquared < radius * radius) {
                        GlowBlock pocket = block.getWorld().getBlockAt(x, y, z);
                        pocket.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
