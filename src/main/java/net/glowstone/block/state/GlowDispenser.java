package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.DispenserEntity;
import net.glowstone.dispenser.*;
import net.glowstone.util.InventoryUtil;
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
        ArmorDispenseBehavior armorDispenseBehavior = new ArmorDispenseBehavior();
        getDispenseBehaviorRegistry().putBehavior(Material.WATER_BUCKET, bucketDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.LAVA_BUCKET, bucketDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.BUCKET, new EmptyBucketDispenseBehavior());
        getDispenseBehaviorRegistry().putBehavior(Material.FLINT_AND_STEEL, new FlintAndSteelDispenseBehavior());
        getDispenseBehaviorRegistry().putBehavior(Material.TNT, new TNTDispenseBehavior());
        getDispenseBehaviorRegistry().putBehavior(Material.LEATHER_BOOTS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.LEATHER_LEGGINGS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.LEATHER_CHESTPLATE, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.LEATHER_HELMET, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.GOLD_BOOTS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.GOLD_LEGGINGS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.GOLD_CHESTPLATE, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.GOLD_HELMET, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.IRON_BOOTS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.IRON_LEGGINGS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.IRON_CHESTPLATE, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.IRON_HELMET, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.CHAINMAIL_BOOTS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.CHAINMAIL_LEGGINGS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.CHAINMAIL_CHESTPLATE, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.CHAINMAIL_HELMET, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.DIAMOND_BOOTS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.DIAMOND_LEGGINGS, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.DIAMOND_CHESTPLATE, armorDispenseBehavior);
        getDispenseBehaviorRegistry().putBehavior(Material.DIAMOND_HELMET, armorDispenseBehavior);
    }

    private DispenserEntity getBlockEntity() {
        return (DispenserEntity) getBlock().getBlockEntity();
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
        
        boolean isEmpty = true;
        for (ItemStack stack : inv.getContents()) {
            if (!InventoryUtil.isEmpty(stack)) {
                isEmpty = false;
                break;
            }
        } 
        if (!isEmpty) {
            int slot = 0;
            int tries = 0;
            while (true) {
                //Gets a random slot and determines if that slot has an item in it
                slot = random.nextInt(9);
                if (inv.getItem(slot).getType() != Material.AIR) break;
                
                //Eliminates the possibility of an infinite loop, goes in order upon reaching 20 random tries
                tries += 1;
                if (tries == 20) {
                    int i = 0;
                    while (i < 9) {
                        if (inv.getItem(i).getType() != Material.AIR) {
                            slot = i;
                            break;
                        }
                    }
                    break;
                }
            }

            return slot;
        } else {
            return 0;
        }
    }

    public ItemStack placeInDispenser(ItemStack toPlace) {
        Inventory inv = getInventory();
        Map<Integer, ItemStack> map = inv.addItem(toPlace);
        return map.isEmpty() ? null : map.get(0);
    }

    @Override
    public Inventory getInventory() {
        return getBlockEntity().getInventory();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        ItemStack[] contents = getInventory().getContents();

        boolean result = super.update(force, applyPhysics);

        if (result) {
            getBlockEntity().setContents(contents);
            getBlockEntity().updateInRange();
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
