package net.glowstone.generator.objects.trees;

import java.util.Random;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Dirt;
import org.bukkit.material.types.DirtType;

public class MegaPineTree extends MegaRedwoodTree {

    /**
     * Initializes this tree with a random height, preparing it to attempt to generate.
     *
     * @param random the PRNG
     * @param location the base of the trunk
     * @param delegate the BlockStateDelegate used to check for space and to fill wood and leaf
     *     blocks
     */
    public MegaPineTree(Random random, Location location, BlockStateDelegate delegate) {
        super(random, delegate);
        setLeavesHeight(random.nextInt(5) + 3);
    }

    @Override
    public boolean generate(Location loc) {
        boolean generated = super.generate(loc);
        if (generated) {
            generatePodzol(loc);
        }
        return generated;
    }

    @Override
    protected void generateDirtBelowTrunk(Location loc) {
        // SELF, SOUTH, EAST, SOUTH EAST
        Dirt dirt = new Dirt(DirtType.PODZOL);
        delegate
            .setTypeAndData(loc.getWorld(), loc.getBlockX(), loc
                            .getBlockY() - 1, loc.getBlockZ(),
                Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX(), loc.getBlockY() - 1,
            loc.getBlockZ() + 1, Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1,
            loc.getBlockZ(), Material.DIRT, dirt);
        delegate.setTypeAndData(loc.getWorld(), loc.getBlockX() + 1, loc.getBlockY() - 1,
            loc.getBlockZ() + 1, Material.DIRT, dirt);
    }

    private void generatePodzol(Location loc) {
        int sourceX = loc.getBlockX();
        int sourceY = loc.getBlockY();
        int sourceZ = loc.getBlockZ();
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ - 1, loc.getWorld());
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ - 1, loc.getWorld());
        generatePodzolPatch(sourceX - 1, sourceY, sourceZ + 2, loc.getWorld());
        generatePodzolPatch(sourceX + 2, sourceY, sourceZ + 2, loc.getWorld());
        for (int i = 0; i < 5; i++) {
            int n = random.nextInt(64);
            if (n % 8 == 0 || n % 8 == 7 || n / 8 == 0 || n / 8 == 7) {
                generatePodzolPatch(sourceX - 3 + n % 8, sourceY, sourceZ - 3 + n / 8, loc.getWorld());
            }
        }
    }

    private void generatePodzolPatch(int sourceX, int sourceY, int sourceZ, World world) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) != 2 || Math.abs(z) != 2) {
                    for (int y = 2; y >= -3; y--) {
                        Block block = world
                            .getBlockAt(sourceX + x, sourceY + y, sourceZ + z);
                        if (block.getType() == Material.GRASS || block.getType() == Material.DIRT) {
                            BlockState state = block.getState();
                            state.setType(Material.DIRT);
                            DirtType dirtType = DirtType.PODZOL;
                            if (world.getBlockAt(sourceX + x, sourceY + y + 1, sourceZ + z)
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
