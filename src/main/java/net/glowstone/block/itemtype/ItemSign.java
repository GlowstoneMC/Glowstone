package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemSign extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        BlockType placeAs;
        if (face == BlockFace.UP) {
            placeAs = ItemTable.instance().getBlock(Material.SIGN_POST);
        } else if (face == BlockFace.DOWN) {
            return;
        } else {
            placeAs = ItemTable.instance().getBlock(Material.WALL_SIGN);
        }
        placeAs.rightClickBlock(player, target, face, holding, clickedLoc, hand);
    }

}
