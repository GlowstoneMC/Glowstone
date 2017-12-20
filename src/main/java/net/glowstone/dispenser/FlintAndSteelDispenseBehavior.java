package net.glowstone.dispenser;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.blocktype.BlockDispenser;
import net.glowstone.block.blocktype.BlockTNT;
import net.glowstone.block.itemtype.ItemTool;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    private boolean successful = true;

    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        GlowBlock target = block.getRelative(BlockDispenser.getFacing(block));

        successful = true;
        if (target.getType() == Material.AIR) {
            target.setType(Material.FIRE);
            InventoryUtil.damageItem(null, stack);
        } else if (target.getType() == Material.TNT) {
            BlockTNT.igniteBlock(target, false);
        } else {
            successful = false;
        }

        return stack.getAmount() > 0 ? stack : null;
    }

    @Override
    protected void playDispenseSound(GlowBlock block) {
        Effect effect = successful ? Effect.CLICK2 : Effect.CLICK1;
        block.getWorld().playEffect(block.getLocation(), effect, 0);
    }
}
