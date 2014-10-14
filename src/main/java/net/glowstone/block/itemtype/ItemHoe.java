package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemHoe extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face, ItemStack holding, Vector clickedLoc) {
        if ((target.getType() == Material.GRASS || target.getType() == Material.DIRT) &&
                target.getRelative(BlockFace.UP).getType() == Material.AIR) {
            target.getWorld().playSound(target.getLocation().add(0.5D, 0.5D, 0.5D), Sound.STEP_GRAVEL, 1, 0.8F);
            target.setType(Material.SOIL);
        }
    }
}
