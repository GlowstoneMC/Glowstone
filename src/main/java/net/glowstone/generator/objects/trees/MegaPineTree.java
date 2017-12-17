package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

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

    @Override
    protected void generateDirtBelowTrunk() {
        // SELF, SOUTH, EAST, SOUTH EAST
        Dirt dirt = new Dirt(DirtType.PODZOL);
        delegate
            .setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ(),
                Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1,
            loc.getBlockZ() + 1, Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1,
            loc.getBlockZ(), Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1,
            loc.getBlockZ() + 1, Material.DIRT, dirt);
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
                generatePodzolPatch(sourceX - 3 + n % 8, sourceY, sourceZ - 3 + n / 8);
            }
        }
    }

    private void generatePodzolPatch(int sourceX, int sourceY, int sourceZ) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    for (int y = 2; y >= -3; y--) {
                        Block block = loc.getWorld()
                            .getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (block.getType() == Material.GRASS || block.getType() == Material.DIRT) {
                            BlockState state = block.getState();
                            state.setType(Material.DIRT);
                            DirtType dirtType = DirtType.PODZOL;
                            if (loc.getWorld().getBlockAt(sourceX + x, sourceY + y + 1, sourceZ + z)
                                .getType().isOccluding()) {
                                dirtType = DirtType.NORMAL;
                            }
                            state.setData(new Dirt(dirtType));
                            state.update(true);
                        } else if (!block.isEmpty() && sourceY + y < sourceY) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
