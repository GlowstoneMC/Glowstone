package net.glowstone.dispenser;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import net.glowstone.block.blocktype.BlockTnt;
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
            // TODO: Find the slot, so we can damage the flint and steel and write it back to the
            // inventory
            // InventoryUtil.damageItem(null, stack);
        } else if (target.getType() == Material.TNT) {
            BlockTnt.igniteBlock(target, false);
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
