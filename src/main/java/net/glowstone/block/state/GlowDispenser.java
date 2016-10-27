package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.TEDispenser;
import net.glowstone.dispenser.*;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

public class GlowDispenser extends GlowLootableBlock implements Dispenser, BlockProjectileSource {

    private static final Random random = new Random();

    private static final DispenseBehaviorRegistry dispenseBehaviorRegistry = new DispenseBehaviorRegistry();

    public GlowDispenser(GlowBlock block) {
        super(block);
    }

    public static DispenseBehaviorRegistry getDispenseBehaviorRegistry() {
        return dispenseBehaviorRegistry;
    }

    public static void register() {
        // register all dispense behaviors
        DefaultDispenseBehavior bucketDispenseBehavior = new BucketDispenseBehavior();
        getDispenseBehaviorRegistry().putBehavior(Material.WATER_BUCKET, bucketDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.LAVA_BUCKET, bucketDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.BUCKET, new EmptyBucketDispenseBehavior());
        getDispenseBehaviorRegistry().putBehavior(Material.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        getDispenseBehaviorRegistry().putBehavior(Material.TNT, new TNTDispenseBehavior());
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
        GlowBlock block = getBlock();

        int dispenseSlot = getDispenseSlot();
        if (dispenseSlot < 0) {
            block.getWorld().playEffect(block.getLocation(), Effect.CLICK1, 0);
            return false;
        }

        ItemStack origItems = getInventory().getItem(dispenseSlot);

        DispenseBehavior behavior = getDispenseBehaviorRegistry().getBehavior(origItems.getType());
        ItemStack result = behavior.dispense(block, origItems);
        getInventory().setItem(dispenseSlot, result);
        return true;
    }

    public int getDispenseSlot() {
        Inventory inv = getInventory();

        int slot = -1;
        int randomChance = 1;

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) != null && random.nextInt(randomChance++) == 0) {
                slot = i;
            }
        }

        return slot;
    }

    public ItemStack placeInDispenser(ItemStack toPlace) {
        Inventory inv = getInventory();
        Map<Integer, ItemStack> map = inv.addItem(toPlace);
        return map.isEmpty() ? null : map.get(0);
    }

    @Override
    public Inventory getInventory() {
        return getTileEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        ItemStack[] contents = getInventory().getContents();

        boolean result = super.update(force, applyPhysics);

        if (result) {
            getTileEntity().setContents(contents);
            getTileEntity().updateInRange();
        }

        return result;
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
