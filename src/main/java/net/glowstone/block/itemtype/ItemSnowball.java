package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowProjectile;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class ItemSnowball extends ItemProjectile {
    public ItemSnowball() {
        super(EntityType.SNOWBALL);
    }

    @Override
    public GlowProjectile throwProjectile(GlowPlayer player, ItemStack stack) {
        return super.throwProjectile(player.getEyeLocation(), player.getVelocity(), 0.0F, 1.5F);
    }
}
