package net.glowstone.block.itemtype;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;

public class ItemBow extends ItemTimedUsage {
    private static final long TICKS_TO_FULLY_CHARGE = 20;
    private static final double MAX_BASE_DAMAGE = 9;
    private static final double MAX_SPEED = 53;
    private ItemStack currentArrow;

    @Override
    public void startUse(GlowPlayer player, ItemStack item) {
        findArrow: for (ItemStack itemStack : player.getInventory().getContents()) {
            switch (itemStack.getType()) {
                case ARROW:
                    if (currentArrow == null) {
                        currentArrow = itemStack;
                    }
                    break;
                case SPECTRAL_ARROW:
                case TIPPED_ARROW:
                    currentArrow = itemStack;
                    break findArrow;
                default:
                    // do nothing
            }
        }
        if (currentArrow != null) {
            player.setUsageItem(item);
            player.setUsageTime(TICKS_TO_FULLY_CHARGE);
        }
    }

    @Override
    public void endUse(GlowPlayer player, ItemStack item) {
        Arrow launchedArrow = null;
        boolean consumeArrow = (player.getGameMode() != GameMode.CREATIVE);
        Material arrowType = currentArrow.getType();
        switch (arrowType) {
            case ARROW:
                if (item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
                    consumeArrow = false;
                }
                launchedArrow = player.launchProjectile(Arrow.class);
                break;
            case TIPPED_ARROW:
                launchedArrow = player.launchProjectile(TippedArrow.class);
                TippedArrow launchedTippedArrow = (TippedArrow) launchedArrow;
                InventoryUtil.copyPotionDataToTippedArrow(launchedTippedArrow, currentArrow);
                break;
            case SPECTRAL_ARROW:
                launchedArrow = player.launchProjectile(SpectralArrow.class);
                break;
            default:
                player.getServer().getLogger().severe(String.format("Attempt to fire a %s from a bow",
                        arrowType));

        }
        if (launchedArrow != null) {
            if (consumeArrow) {
                int amount = currentArrow.getAmount();
                if (amount <= 1) {
                    player.getInventory().remove(currentArrow);
                } else {
                    currentArrow.setAmount(amount - 1);
                }
            }
            double chargeFraction = Math.max(0.0,
                    1.0 - (TICKS_TO_FULLY_CHARGE - player.getUsageTime()) / TICKS_TO_FULLY_CHARGE);
            double damage = MAX_BASE_DAMAGE * chargeFraction
                    * (1 + 0.25 * item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
            launchedArrow.setVelocity(launchedArrow.getVelocity().normalize().multiply(
                    chargeFraction * MAX_SPEED));
            launchedArrow.spigot().setDamage(damage);
            if (item.containsEnchantment(Enchantment.ARROW_FIRE)) {
                // Arrow will burn as long as it's in flight, unless extinguished by water
                launchedArrow.setFireTicks(Integer.MAX_VALUE);
            }
            launchedArrow
                    .setKnockbackStrength(item.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK));
            // 20% crit chance
            if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                launchedArrow.setCritical(true);
            }
        }
        InventoryUtil.damageItem(player, item);
        player.setUsageItem(null);
        player.setUsageTime(0);
        currentArrow = null;
    }
}
