package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.inventory.ItemStack;

public class ItemExperienceBottle extends ItemProjectile {
    public ItemExperienceBottle() {
        super(EntityType.THROWN_EXP_BOTTLE);
    }

    @Override
    public Projectile use(GlowPlayer player, ItemStack holding) {
        return player.launchProjectile(ThrownExpBottle.class);
    }
}
