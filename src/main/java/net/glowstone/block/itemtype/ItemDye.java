package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.CocoaPlant.CocoaPlantSize;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;
import org.bukkit.util.Vector;

public class ItemDye extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc) {
        MaterialData data = holding.getData();
        if (data instanceof Dye) {
            final Dye dye = (Dye) data;
            if (dye.getColor() == DyeColor.BROWN && target.getType() == Material.LOG) {
                data = target.getState().getData();
                if (data instanceof Tree &&
                        ((Tree) data).getSpecies() == TreeSpecies.JUNGLE &&
                        target.getRelative(face).getType() == Material.AIR) {
                    final GlowBlockState state = target.getRelative(face).getState();
                    state.setType(Material.COCOA);
                    state.setData(new CocoaPlant(CocoaPlantSize.SMALL, face.getOppositeFace()));
                    state.update(true);

                    // deduct from stack if not in creative mode
                    if (player.getGameMode() != GameMode.CREATIVE) {
                        holding.setAmount(holding.getAmount() - 1);
                    }
                }
            }
        }
    }
}
