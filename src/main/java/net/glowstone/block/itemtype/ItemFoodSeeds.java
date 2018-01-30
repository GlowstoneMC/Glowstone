package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * A type of edible item that can be planted on one specific type of block, such as a carrot or
 * potato.
 */
public class ItemFoodSeeds extends ItemFood {

    private Material cropsType;
    private Material soilType;

    /**
     * Creates an instance.
     *
     * @param cropsType this item's block form
     * @param soilType the type of block this can be planted on
     * @param food the amount of hunger this food fills, in half icons
     * @param saturation the amount of saturation this food grants, in half icons saved
     */
    public ItemFoodSeeds(Material cropsType, Material soilType, int food, float saturation) {
        super(food, saturation);
        this.cropsType = cropsType;
        this.soilType = soilType;
    }

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock target, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        if (target.getType() == soilType
            && target.getRelative(BlockFace.UP).getType() == Material.AIR && face == BlockFace.UP) {
            GlowBlockState state = target.getRelative(BlockFace.UP).getState();
            state.setType(cropsType);
            state.setRawData((byte) 0);
            state.update(true);

            // deduct from stack if not in creative mode
            if (player.getGameMode() != GameMode.CREATIVE) {
                holding.setAmount(holding.getAmount() - 1);
            }
        }
    }
}
