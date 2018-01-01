package net.glowstone.dispenser;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import net.glowstone.entity.GlowTntPrimed;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class TntDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        GlowWorld world = block.getWorld();
        GlowBlock target = block.getRelative(BlockDispenser.getFacing(block));
        GlowTntPrimed tnt = (GlowTntPrimed) world
            .spawnEntity(target.getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        world.playSound(tnt.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
        stack.setAmount(stack.getAmount() - 1);
        return stack.getAmount() > 0 ? stack : null;
    }
}
