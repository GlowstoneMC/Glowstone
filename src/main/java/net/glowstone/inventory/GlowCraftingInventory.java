package net.glowstone.inventory;

import java.util.Arrays;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.crafting.CraftingManager;
import net.glowstone.util.InventoryUtil;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * Represents a crafting grid inventory, both workbench and per-player.
 */
public class GlowCraftingInventory extends GlowInventory implements CraftingInventory {

    private static final int RESULT_SLOT = 0;
    private static final int MATRIX_START = 1;

    /**
     * Create an inventory for the 2x2 or 3x3 crafting grid.
     *
     * @param owner the crafting player
     * @param type {@link InventoryType#CRAFTING} or {@link InventoryType#WORKBENCH}
     */
    public GlowCraftingInventory(InventoryHolder owner, InventoryType type) {
        super(owner, type);
        if (type != InventoryType.CRAFTING && type != InventoryType.WORKBENCH) {
            throw new IllegalArgumentException(
                "GlowCraftingInventory cannot be " + type + ", only CRAFTING or WORKBENCH.");
        }

        getSlot(RESULT_SLOT).setType(SlotType.RESULT);
        for (int i = MATRIX_START; i < getSize(); i++) {
            getSlot(i).setType(SlotType.CRAFTING);
        }
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);

        if (index != RESULT_SLOT) {
            this.updateResultSlot();
        }
    }

    @Override
    public boolean itemShiftClickAllowed(int slot, ItemStack stack) {
        // cannot ever shift-click into a crafting inventory
        return false;
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot,
        ItemStack clickedItem) {
        if (getSlotType(view.convertSlot(clickedSlot)) == SlotType.RESULT) {
            // If the player clicked on the result give it to them
            Recipe recipe = getRecipe();
            if (recipe == null) {
                return; // No complete recipe in crafting grid
            }

            final ItemStack[] matrix = getMatrix();

            // Set to correct amount (tricking the client and click handler)
            int recipeAmount = CraftingManager.getLayers(matrix);
            clickedItem.setAmount(clickedItem.getAmount() * recipeAmount);

            // Place the items in the player's inventory (right to left)
            player.getInventory().tryToFillSlots(clickedItem, 8, -1, 35, 8);

            // Avoid calling craft because we already know the player can craft 'recipeAmount' of
            // this item
            CraftingManager cm = player.getServer().getCraftingManager();
            // Removing all the items at once will avoid multiple useless calls to craft
            // (and all of its sub methods like getRecipe)
            cm.removeItems(matrix, this, recipeAmount);
        } else {
            // Clicked in the crafting grid, no special handling required
            // (just place them left to right)
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
        CraftingManager cm = ((GlowServer) ServerProvider.getServer()).getCraftingManager();
        Recipe recipe = cm.getCraftingRecipe(matrix);

        if (recipe != null) {
            cm.removeItems(matrix, this);
        }
    }

    @Override
    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    @Override
    public void setResult(ItemStack newResult) {
        setItem(RESULT_SLOT, newResult);
    }

    @Override
    public ItemStack[] getMatrix() {
        return Arrays.copyOfRange(getContents(), MATRIX_START, getSize());
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

        this.updateResultSlot();
    }

    @Override
    public Recipe getRecipe() {
        return ((GlowServer) ServerProvider.getServer()).getCraftingManager()
            .getCraftingRecipe(getMatrix());
    }

    /**
     * Update the result slot with the current matrix.
     */
    public void updateResultSlot() {
        Recipe recipe = getRecipe();
        if (recipe == null) {
            super.setItem(RESULT_SLOT, InventoryUtil.createEmptyStack());
        } else {
            super.setItem(RESULT_SLOT, recipe.getResult());
        }
    }

}
