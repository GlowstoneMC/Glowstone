package net.glowstone.inventory;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.InventoryUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class GlowAnvilInventory extends GlowInventory implements AnvilInventory {

    private static final int FIRST_ITEM_SLOT = 0;
    private static final int SECOND_ITEM_SLOT = 1;
    private static final int RESULT_SLOT = 2;
    @Getter
    private String renameText = "";
    @Getter
    @Setter
    private int repairCost;
    @Getter
    @Setter
    private int maximumRepairCost;

    /**
     * Creates an instance for the given player.
     * @param holder the player
     */
    public GlowAnvilInventory(InventoryHolder holder) {
        super(holder, InventoryType.ANVIL);

        maximumRepairCost = 40;

        getSlot(FIRST_ITEM_SLOT).setType(SlotType.CRAFTING);
        getSlot(SECOND_ITEM_SLOT).setType(SlotType.CRAFTING);
        getSlot(RESULT_SLOT).setType(SlotType.RESULT);
    }

    @Override
    public int getRawSlots() {
        return 0;
    }

    @Override
    public int getRepairCostAmount() {
        return 0;
    }

    @Override
    public void setRepairCostAmount(int amount) {

    }

    public ItemStack getFirstItem() {
        return getSlot(FIRST_ITEM_SLOT).getItem();
    }

    public ItemStack getSecondItem() {
        return getSlot(SECOND_ITEM_SLOT).getItem();
    }

    public ItemStack getResultItem() {
        return getSlot(RESULT_SLOT).getItem();
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot,
        ItemStack clickedItem) {
        if (getSlotType(view.convertSlot(clickedSlot)) == SlotType.RESULT) {
            // If the player clicked on the result give it to them
            ItemStack forged = getForged();
            if (forged == null) {
                return; // we can't smith, my liege
            }

            // Smith the item
            smith();

            // Place the item in the player's inventory (right to left)
            player.getInventory().tryToFillSlots(clickedItem, 8, -1, 35, 8);
        } else {
            // Clicked in the crafting grid, no special handling required (just place them left to
            // right)
            clickedItem = player.getInventory().tryToFillSlots(clickedItem, 9, 36, 0, 9);
            view.setItem(clickedSlot, clickedItem);
        }
    }

    private void smith() {
        setItem(FIRST_ITEM_SLOT, InventoryUtil.createEmptyStack());
        setItem(SECOND_ITEM_SLOT, InventoryUtil.createEmptyStack());
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);

        if (index != RESULT_SLOT) {
            ItemStack forged = getForged();
            if (forged == null) {
                super.setItem(RESULT_SLOT, InventoryUtil.createEmptyStack());
            } else {
                super.setItem(RESULT_SLOT, forged);
            }
        }
    }

    /**
     * Returns the item that will result when this anvil is applied to the currently loaded items,
     * combining them, provided that two items which can be combined are loaded. A return of null
     * doesn't imply that the anvil cannot be activated, since it may still be able to repair/name a
     * single item.
     *
     * @return the resulting item, or null if two items that can be combined are not loaded
     */
    public ItemStack getForged() {
        if (InventoryUtil.isEmpty(getFirstItem()) || InventoryUtil.isEmpty(getSecondItem())) {
            return null;
        }
        if (getSecondItem().getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta) getSecondItem().getItemMeta();
            ItemStack result;
            if (InventoryUtil.isEmpty(getResultItem())) {
                result = getFirstItem().clone();
            } else {
                result = getResultItem();
            }
            book.getStoredEnchants().forEach((enchantment, level) -> {
                if (enchantment.canEnchantItem(result)
                    || result.getType() == Material.ENCHANTED_BOOK) {
                    result.addUnsafeEnchantment(enchantment, level);
                }
            });
            return result;
        }
        return null;
    }

    /**
     * Sets the name of the next item to be modified on this anvil.
     *
     * @param name the item name
     */
    public void setRenameText(String name) {
        renameText = name;
        if (renameText.isEmpty()) {
            setItem(FIRST_ITEM_SLOT, getFirstItem());
            setItem(SECOND_ITEM_SLOT, getSecondItem());
        } else {
            ItemStack result = getFirstItem().clone();
            if (!InventoryUtil.isEmpty(result)) {
                if (Objects.equals(result.getItemMeta().getDisplayName(), name)) {
                    setItem(RESULT_SLOT, InventoryUtil.createEmptyStack());
                }
                // rename the item
                ItemMeta m = result.getItemMeta();
                m.setDisplayName(ChatColor.ITALIC + renameText);
                result.setItemMeta(m);
                setItem(RESULT_SLOT, result);
            }
        }
    }
}
