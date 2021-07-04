package net.glowstone.inventory.crafting;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowRepairMatcher extends ItemMatcher {

    private static boolean isRepairable(ItemStack item) {
        return EnchantmentTarget.ARMOR.includes(item)
            || EnchantmentTarget.TOOL.includes(item)
            || EnchantmentTarget.WEAPON.includes(item)
            || EnchantmentTarget.BOW.includes(item)
            || EnchantmentTarget.FISHING_ROD.includes(item);
    }

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) {
                continue;
            }

            if (!isRepairable(item)) {
                return null; // Non-repairable item in matrix
            }

            items.add(item);
        }

        if (items.size() != 2) {
            return null; // Can only have 2 tools
        }

        ItemStack itemA = items.get(0);
        ItemStack itemB = items.get(1);

        if (itemA.getType() != itemB.getType()) {
            return null; // Not same item type
        }

        Material type = itemA.getType();

        int usesA = type.getMaxDurability() - itemA.getDurability();
        int usesB = type.getMaxDurability() - itemB.getDurability();
        int totalUses = (int) (usesA + usesB + type.getMaxDurability() * 0.05);
        int damage = type.getMaxDurability() - totalUses;

        return new ItemStack(type, 1, (short) Math.max(damage, 0));
    }
}
