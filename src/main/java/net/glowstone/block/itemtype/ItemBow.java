package net.glowstone.block.itemtype;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventorySlot;
import net.glowstone.util.InventoryUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

public class ItemBow extends ItemTimedUsage {
    private static final long TICKS_TO_FULLY_CHARGE = 20;
    private static final double MAX_BASE_DAMAGE = 9;
    private static final double MAX_SPEED = 53;
    private Class<? extends Arrow> currentArrowType;
    private PotionMeta currentTippedArrowMeta;

    @Override
    public void startUse(GlowPlayer player, ItemStack item) {
        ItemStack arrow = null;
        GlowInventorySlot arrowSlot = null;
        findArrow: for (GlowInventorySlot slot : player.getInventory().getSlots()) {
            ItemStack itemStack = arrowSlot.getItem();
            switch (itemStack.getType()) {
                case ARROW:
                    arrow = itemStack;
                    arrowSlot = slot;
                    currentArrowType = Arrow.class;
                    break;
                case SPECTRAL_ARROW:
                    arrow = itemStack;
                    arrowSlot = slot;
                    currentArrowType = SpectralArrow.class;
                    break findArrow;
                case TIPPED_ARROW:
                    arrow = itemStack;
                    arrowSlot = slot;
                    currentArrowType = TippedArrow.class;
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta instanceof PotionMeta) {
                        currentTippedArrowMeta = (PotionMeta) itemMeta;
                    }
                    break findArrow;
                default:
                    // do nothing
            }
        }
        if (arrow != null) {
            if (currentArrowType != Arrow.class
                    || !item.containsEnchantment(Enchantment.ARROW_INFINITE)) {
                // Consume the arrow
                int amount = arrow.getAmount();
                if (amount > 1) {
                    arrow.setAmount(arrow.getAmount() - 1);
                } else {
                    arrowSlot.setItem(InventoryUtil.createEmptyStack());
                }
            }
            player.setUsageItem(item);
            player.setUsageTime(TICKS_TO_FULLY_CHARGE);
        }
    }

    @Override
    public void endUse(GlowPlayer player, ItemStack item) {
        Arrow launchedArrow = player.launchProjectile(currentArrowType);
        if (currentArrowType == TippedArrow.class) {
            TippedArrow launchedTippedArrow = (TippedArrow) launchedArrow;
            launchedTippedArrow.setBasePotionData(currentTippedArrowMeta.getBasePotionData());
            launchedTippedArrow.setColor(currentTippedArrowMeta.getColor());
            for (PotionEffect effect : currentTippedArrowMeta.getCustomEffects()) {
                launchedTippedArrow.addCustomEffect(effect, true);
            }
            currentTippedArrowMeta = null;
        }
        double chargeFraction = Math.max(0.0,
                1.0 - (TICKS_TO_FULLY_CHARGE - player.getUsageTime()) / TICKS_TO_FULLY_CHARGE);
        double damage = MAX_BASE_DAMAGE * chargeFraction
                * (1 + 0.25 * item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE));
        launchedArrow.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(
                chargeFraction * MAX_SPEED));
        launchedArrow.spigot().setDamage(damage);
        if (item.containsEnchantment(Enchantment.ARROW_FIRE)) {
            // Arrow will burn as long as it's in flight, unless extinguished by water
            launchedArrow.setFireTicks(Integer.MAX_VALUE);
        }
        launchedArrow.setKnockbackStrength(item.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK));
        // 20% crit chance
        if (ThreadLocalRandom.current().nextDouble() < 0.2) {
            launchedArrow.setCritical(true);
        }
        player.setUsageItem(null);
        player.setUsageTime(0);
    }
}
