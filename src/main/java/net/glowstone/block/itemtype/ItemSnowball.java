package net.glowstone.block.itemtype;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;

public class ItemSnowball extends ItemProjectile {
    public ItemSnowball() {
        super(EntityType.SNOWBALL);
    }

    @Override
    public Projectile use(GlowPlayer player, ItemStack holding) {
        return player.launchProjectile(Snowball.class);
    }
}
