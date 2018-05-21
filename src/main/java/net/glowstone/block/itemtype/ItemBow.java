package net.glowstone.block.itemtype;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.inventory.GlowInventorySlot;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class ItemBow extends ItemTimedUsage {
    private static final long TICKS_TO_FULLY_CHARGE = 20;
    private static final double MAX_BASE_DAMAGE = 9;
    private static final double MAX_SPEED = 53;

    @Override
    public void startUse(GlowPlayer player, ItemStack item) {
        if (player.getGameMode() == GameMode.CREATIVE || findArrow(player).isPresent()) {
            player.setUsageItem(item);
            player.setUsageTime(TICKS_TO_FULLY_CHARGE);
        }
    }

    @Override
    public void endUse(GlowPlayer player, ItemStack bow) {
        // Check arrows again, since plugins may have changed the inventory while the bow was drawn
        Optional<GlowInventorySlot> maybeArrow = findArrow(player);
        GlowInventorySlot slot = null;
        ItemStack arrow = null;
        final Material arrowType;
        Arrow launchedArrow = null;
        boolean consumeArrow = false;
        if (maybeArrow.isPresent()) {
            slot = maybeArrow.get();
            arrow = slot.getItem();
            arrowType = arrow.getType();
            consumeArrow = (player.getGameMode() != GameMode.CREATIVE);
        } else if (player.getGameMode() == GameMode.CREATIVE) {
            // Can fire without arrows in Creative
            arrowType = Material.ARROW;
            consumeArrow = false;
        } else {
            arrowType = Material.AIR;
        }
        switch (arrowType) {
            case ARROW:
                if (bow.containsEnchantment(Enchantment.ARROW_INFINITE)) {
                    consumeArrow = false;
                }
                launchedArrow = player.launchProjectile(Arrow.class);
                break;
            case TIPPED_ARROW:
                launchedArrow = player.launchProjectile(TippedArrow.class);
                GlowTippedArrow launchedTippedArrow = (GlowTippedArrow) launchedArrow;
                launchedTippedArrow.copyFrom((PotionMeta) arrow.getItemMeta());
                break;
            case SPECTRAL_ARROW:
                launchedArrow = player.launchProjectile(SpectralArrow.class);
                break;
            case AIR:
                // Not in creative mode and have no arrow
                break;
            default:
                GlowServer.logger.log(Level.SEVERE, () ->
                        String.format("Attempt to fire %s from a bow", arrowType));

        }
        if (launchedArrow != null) {
            if (consumeArrow) {
                int amount = arrow.getAmount();
                if (amount <= 1) {
                    arrow = InventoryUtil.createEmptyStack();
                } else {
                    arrow.setAmount(amount - 1);
                }
                slot.setItem(arrow);
            }
            double chargeFraction = Math.max(0.0,
                    1.0 - (TICKS_TO_FULLY_CHARGE - player.getUsageTime())
                            / TICKS_TO_FULLY_CHARGE);
            double damage = MAX_BASE_DAMAGE * chargeFraction
                    * (1 + 0.25 * bow.getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
            launchedArrow.setVelocity(player.getEyeLocation().getDirection().multiply(
                    chargeFraction * MAX_SPEED));
            launchedArrow.spigot().setDamage(damage);
            if (bow.containsEnchantment(Enchantment.ARROW_FIRE)) {
                // Arrow will burn as long as it's in flight, unless extinguished by water
                launchedArrow.setFireTicks(Integer.MAX_VALUE);
            }
            launchedArrow
                    .setKnockbackStrength(bow
                            .getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK));
            // 20% crit chance
            if (ThreadLocalRandom.current().nextDouble() < 0.2) {
                launchedArrow.setCritical(true);
            }

            if (player.getItemInHand() == bow) {
                player.setItemInHand(InventoryUtil.damageItem(player, bow));
            } else {
                player.getInventory().setItemInOffHand(InventoryUtil.damageItem(player, bow));
            }
        }
        player.setUsageItem(null);
        player.setUsageTime(0);
    }

    private Optional<GlowInventorySlot> findArrow(GlowPlayer player) {
        Optional<GlowInventorySlot> currentArrow = Optional.empty();
        for (GlowInventorySlot itemSlot : player.getInventory().getSlots()) {
            ItemStack itemStack = itemSlot.getItem();
            switch (itemStack.getType()) {
                case SPECTRAL_ARROW:
                case TIPPED_ARROW:
                    return Optional.of(itemSlot);
                case ARROW:
                    currentArrow = Optional.of(itemSlot);
                    continue; // keep looking, in case we find a special arrow
                default:
                    // do nothing
            }
        }
        return currentArrow;
    }
}
