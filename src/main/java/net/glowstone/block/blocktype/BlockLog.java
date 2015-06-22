package net.glowstone.block.blocktype;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class BlockLog extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Tree) {
            ((Tree) data).setDirection(face);
            ((Tree) data).setSpecies(TreeSpecies.getByData((byte) holding.getDurability()));
        } else {
            warnMaterialData(Tree.class, data);
        }
        state.setData(data);
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        return Collections.singletonList(new ItemStack(Material.LOG, 1, (short) (block.getData() & 0x03)));
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        // vanilla set leaf decay check in a 9x9x9 neighboring when a log block is removed
        final GlowWorld world = block.getWorld();
        for (int x = 0; x < 9; x++) {
            for (int z = 0; z < 9; z++) {
                for (int y = 0; y < 9; y++) {
                    final GlowBlock b = world.getBlockAt(block.getLocation().add(x - 4, y - 4, z - 4));
                    if (b.getType() == Material.LEAVES || b.getType() == Material.LEAVES_2) {
                        final GlowBlockState state = b.getState();
                        if ((state.getRawData() & 0x08) == 0 && (state.getRawData() & 0x04) == 0) { // check decay is off and decay is on
                            // set decay check on for this leaves block
                            state.setRawData((byte) (state.getRawData() | 0x08));
                            state.update(true);
                        }
                    }
                }
            }
        }
    }
}
