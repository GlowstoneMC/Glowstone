package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class ItemEgg extends ItemProjectile {
    public ItemEgg() {
        super(EntityType.EGG);
    }

    @Override
    public Projectile use(GlowPlayer player, ItemStack holding) {
        return player.launchProjectile(Egg.class);
    }
}
