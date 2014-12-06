package net.glowstone.generator.objects.trees;

import java.util.Random;

import net.glowstone.util.BlockStateDelegate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MegaPineTree extends MegaRedwoodTree {

    public MegaPineTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, location, delegate);
        setLeavesHeight(random.nextInt(5) + 3);
    }

    @Override
    public boolean generate() {
        boolean generated = super.generate();
        if (generated) {
            generatePodzol();
        }
        return generated;
    }

    private void generatePodzol() {
        int sourceX = loc.getBlockX();
        int sourceY = loc.getBlockY();
        int sourceZ = loc.getBlockZ();
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ - 1);
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ - 1);
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ + 2);
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ + 2);
        for (int i = 0; i < 5; i++) {
           int n = random.nextInt(64);
           if (n % 8 == 0 || n % 8 == 7 || n / 8 == 0 || n / 8 == 7) {
               generatePodzolPatch(sourceX - 3 + (n % 8), sourceY, sourceZ - 3 + (n / 8));
           }
        }
    }

    private void generatePodzolPatch(int sourceX, int sourceY, int sourceZ) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    for (int y = 2; y >= -3; y--) {
                        final Block block = loc.getWorld().getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (block.getType() == Material.GRASS || block.getType() == Material.DIRT) {
                            block.setType(Material.DIRT);
                            block.setData((byte) 2);
                        } else if (!block.isEmpty() && sourceY + y < sourceY) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
