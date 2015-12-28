package net.glowstone.inventory;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.crafting.CraftingManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.*;

import java.util.Arrays;

/**
 * Represents a crafting grid inventory, both workbench and per-player.
 */
public class GlowCraftingInventory extends GlowInventory implements CraftingInventory {

    private static final int RESULT_SLOT = 0;
    private static final int MATRIX_START = 1;

    public GlowCraftingInventory(InventoryHolder owner, InventoryType type) {
        super(owner, type);
        if (type != InventoryType.CRAFTING && type != InventoryType.WORKBENCH) {
            throw new IllegalArgumentException("GlowCraftingInventory cannot be " + type + ", only CRAFTING or WORKBENCH.");
        }

        getSlot(RESULT_SLOT).setType(InventoryType.SlotType.RESULT);
        for (int i = MATRIX_START; i < getSize(); i++) {
            getSlot(i).setType(InventoryType.SlotType.CRAFTING);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);

        if (index != RESULT_SLOT) {
            Recipe recipe = getRecipe();
            if (recipe == null) {
                super.setItem(RESULT_SLOT, null);
            } else {
                super.setItem(RESULT_SLOT, recipe.getResult());
            }
        }
    }

    @Override
    public boolean itemShiftClickAllowed(int slot, ItemStack stack) {
        // cannot ever shift-click into a crafting inventory
        return false;
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot, ItemStack clickedItem) {
        if (getSlotType(view.convertSlot(clickedSlot)) == SlotType.RESULT) {
            // If the player clicked on the result give it to them
            Recipe recipe = getRecipe();
            if (recipe == null) {
                return; // No complete recipe in crafting grid
            }

            // Set to correct amount (tricking the client and click handler)
            int recipeAmount = ((GlowServer) Bukkit.getServer()).getCraftingManager().getLayers(getMatrix());
            clickedItem.setAmount(clickedItem.getAmount() * recipeAmount);

            // Place the items in the player's inventory (right to left)
            player.getInventory().tryToFillSlots(clickedItem, 8, -1, 35, 8);

            // Craft the items, removing the ingredients from the crafting matrix
            for (int i = 0; i < recipeAmount; i++) {
                craft();
            }
        } else {
            // Clicked in the crafting grid, no special handling required (just place them left to right)
            clickedItem = player.getInventory().tryToFillSlots(clickedItem, 9, 36, 0, 9);
            view.setItem(clickedSlot, clickedItem);
        }

    }

    @Override
    public int getRawSlots() {
        return 0;
    }

    /**
     * Remove a layer of items from the inventory.
     */
    public void craft() {
        ItemStack[] matrix = getMatrix();
        CraftingManager cm = ((GlowServer) Bukkit.getServer()).getCraftingManager();
        Recipe recipe = cm.getCraftingRecipe(matrix);

        if (recipe != null) {
            craft(cm);
        }
    }

    /**
     * Remove a layer of items from the inventory.
     */
    protected void craft(CraftingManager cm) {
        ItemStack[] matrix = getMatrix();
        cm.removeItems(matrix, this);
    }

    @Override
    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    @Override
    public ItemStack[] getMatrix() {
        return replaceAirWithNull(Arrays.copyOfRange(getContents(), MATRIX_START, getSize()));
    }

    /**
     * CraftingManager expects null for empty slots, not AIRx0. Convert our internal representation to something
     * compatible to correct an error where after crafting the original inputs were dropped (because the craft()
     * method failed to find a matching recipe and thus never decremented the input counts - leaving them "hidden"
     * in the crafting squares but yielding output --> item duplication).
     */
    private ItemStack[] replaceAirWithNull(ItemStack[] items) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].getType() == Material.AIR) {
                items[i] = null;
            }
        }
        return items;
    }

    @Override
    public void setResult(ItemStack newResult) {
        setItem(RESULT_SLOT, newResult);
    }

    @Override
    public void setMatrix(ItemStack[] contents) {
        if (contents.length != getSize() - 1) {
            throw new IllegalArgumentException("Length must be " + (getSize() - 1));
        }
        for (int i = 0; i < contents.length; ++i) {
            // Call super method, so we only calculate the result once
            super.setItem(MATRIX_START + i, contents[i]);
        }
        // Update result
        Recipe recipe = getRecipe();
        if (recipe == null) {
            super.setItem(RESULT_SLOT, null);
        } else {
            super.setItem(RESULT_SLOT, recipe.getResult());
        }
    }

    @Override
    public Recipe getRecipe() {
        return ((GlowServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(getMatrix());
    }

}
