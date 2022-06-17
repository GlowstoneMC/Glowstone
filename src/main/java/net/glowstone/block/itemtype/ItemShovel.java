package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemShovel extends ItemTool {

    @Override
    public boolean onToolRightClick(GlowPlayer player, GlowBlock target, BlockFace face,
                                    ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (target.getRelative(BlockFace.UP).getType() == Material.AIR
            && target.getType() == Material.GRASS_BLOCK && face != BlockFace.DOWN) {
            target.getWorld()
                .playSound(target.getLocation().add(0.5D, 0.5D, 0.5D), Sound.BLOCK_GRAVEL_STEP,
                    1, 0.8F);
            target.setType(Material.DIRT_PATH);
            return true;
        }
        return false;
    }
}
