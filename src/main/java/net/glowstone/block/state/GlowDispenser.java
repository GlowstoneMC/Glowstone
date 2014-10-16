package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEDispenser;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

public class GlowDispenser extends GlowBlockState implements Dispenser, BlockProjectileSource {

    public GlowDispenser(GlowBlock block) {
        super(block);
    }

    private TEDispenser getTileEntity() {
        return (TEDispenser) getBlock().getTileEntity();
    }

    @Override
    public BlockProjectileSource getBlockProjectileSource() {
        return this;
    }

    @Override
    public boolean dispense() {
        // todo: dispense item
        return false;
    }

    @Override
    public Inventory getInventory() {
        return getTileEntity().getInventory();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return launchProjectile(projectile, null);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        // todo: projectile launching
        return null;
    }
}
