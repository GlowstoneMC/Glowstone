package net.glowstone.dispenser;

import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import org.bukkit.Effect;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public abstract class ProjectileDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        GlowWorld world = block.getWorld();
        Vector position = BlockDispenser.getDispensePosition(block);
        BlockFace face = BlockDispenser.getFacing(block);
        Projectile entity = getProjectileEntity(world, position);
        entity.setVelocity(new Vector(face.getModX(), face.getModY() + 0.1f, face.getModZ()).multiply(6));

        stack.setAmount(stack.getAmount() - 1);
        if (stack.getAmount() < 1) {
            return null;
        }
        return stack;
    }

    @Override
    protected void playDispenseSound(GlowBlock block) {
        block.getWorld().playEffect(block.getLocation(), Effect.BOW_FIRE, 0);
    }

    protected abstract Projectile getProjectileEntity(GlowWorld world, Vector position);
}
