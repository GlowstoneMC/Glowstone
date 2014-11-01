package net.glowstone.inventory.crafting;

import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemDamageable;
import net.glowstone.block.itemtype.ItemType;
import org.bukkit.inventory.ItemMatcher;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GlowRepairMatcher extends ItemMatcher {

    @Override
    public ItemStack getResult(ItemStack[] matrix) {
        List<ItemStack> items = new ArrayList<>();

        for (ItemStack item : matrix) {
            if (item == null) continue;

            ItemType type = ItemTable.instance().getItem(item.getType());
            if (!(type instanceof ItemDamageable)) return null; // Non-repairable item in matrix

            items.add(item);
        }

        if (items.size() != 2) return null; // Can only have 2 tools

        ItemStack itemA = items.get(0);
        ItemStack itemB = items.get(1);

        if (itemA.getType() != itemB.getType()) return null; // Not same item type

        ItemDamageable type = (ItemDamageable) ItemTable.instance().getItem(itemA.getType());

        int usesA = type.getMaxUses() - itemA.getDurability();
        int usesB = type.getMaxUses() - itemB.getDurability();
        int totalUses = (int) (usesA + usesB + (type.getMaxUses() * 0.05));
        int damage = type.getMaxUses() - totalUses;

        return new ItemStack(type.getMaterial(), 1, (short) Math.max(damage, 0));
    }
}
