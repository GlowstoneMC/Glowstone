package net.glowstone.generator.decorators.overworld;

import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.generator.decorators.BlockDecorator;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SmallCaveDecorator extends BlockDecorator {

    @Override
    public void decorate(World world, Random random, Chunk c) {
        if (random.nextInt(8) != 0) {
            return;
        }
        GlowChunk chunk = (GlowChunk) c;
        final int startCx = random.nextInt(16), startCz = random.nextInt(16), startY = chunk.getHeight(startCx, startCz);
        if (startY > 128) {
            return;
        }
        final GlowBlock startBlock = chunk.getBlock(startCx, startY, startCz);
        List<BlockVector> ray = new ArrayList<>();
        int rayLength = random.nextInt(150) + 15;
        BlockVector current = new BlockVector();
        for (int i = 0; i < rayLength; i++) {
            float depth = (float) i / (float) rayLength;
            BlockVector vector = randomRayVector(random, depth);
            current.add(vector);
            if (current.getBlockY() + startY > startY + 3 || current.getBlockY() + startY < 5) {
                break;
            }
            ray.add(vector);
        }
        if (ray.size() < 5) {
            return;
        }
        GlowBlock rayStream = startBlock;
        for (BlockVector vector : ray) {
            GlowBlock block = rayStream.getRelative(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            if (block.getType() == Material.BEDROCK) {
                return;
            }
            caveAroundRay(block, random);
            rayStream = block;
        }
    }

    private void caveAroundRay(GlowBlock block, Random random) {
        int radius = random.nextInt(2) + 2;
        final int bX = block.getX(), bY = block.getY(), bZ = block.getZ();
        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++) {
                    double distance = (bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z);
                    if (distance < radius * radius) {
                        GlowBlock pocket = block.getWorld().getBlockAt(x, y, z);
                        pocket.setType(Material.AIR);
                    }
                }
            }
        }
    }

    private BlockVector randomRayVector(Random random, float depth) {
        return new BlockVector(
                (random.nextInt(3) + 2) * (random.nextBoolean() ? 1 : -1),
                random.nextFloat() < depth * 0.5 ? 0 : random.nextInt(4) * (random.nextFloat() < 0.1 + (depth * 0.2) ? 1 : -1),
                (random.nextInt(3) + 2) * (random.nextBoolean() ? 1 : -1));
    }
}
