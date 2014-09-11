package net.glowstone.inventory;

import net.glowstone.GlowServer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

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

        slotTypes[RESULT_SLOT] = InventoryType.SlotType.RESULT;
        Arrays.fill(slotTypes, MATRIX_START, getSize(), InventoryType.SlotType.CRAFTING);
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

    /**
     * Remove a layer of items from the inventory according to the current recipe.
     */
    public void craft() {
        ItemStack[] matrix = getMatrix();
        CraftingManager cm = ((GlowServer) Bukkit.getServer()).getCraftingManager();
        Recipe recipe = cm.getCraftingRecipe(matrix);

        if (recipe != null) {
            cm.removeItems(matrix, recipe);
            setMatrix(matrix);
        }
    }

    @Override
    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    @Override
    public ItemStack[] getMatrix() {
        return Arrays.copyOfRange(getContents(), MATRIX_START, getSize());
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
            setItem(MATRIX_START + i, contents[i]);
        }
    }

    @Override
    public Recipe getRecipe() {
        return ((GlowServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(getMatrix());
    }

}
