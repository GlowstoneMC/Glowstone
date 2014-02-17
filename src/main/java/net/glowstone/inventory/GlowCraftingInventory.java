package net.glowstone.inventory;

import net.glowstone.GlowServer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;

/**
 * Represents the portion of a player's inventory which handles crafting.
 */
public class GlowCraftingInventory extends GlowInventory implements CraftingInventory {

    public static final int RESULT_SLOT = 0;
    private static final int MATRIX_START = 1;

    public GlowCraftingInventory(GlowPlayerInventory parent) {
        super(parent.getHolder(), InventoryType.CRAFTING);
    }

    /**
     * Stores the ItemStack at the given index.
     * Notifies all attached InventoryViewers of the change.
     * @param index The index where to put the ItemStack
     * @param item  The ItemStack to set
     */
    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);

        if (index != RESULT_SLOT) {
            ItemStack[] items = getMatrix();

            Recipe recipe = ((GlowServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(items);
            if (recipe == null) {
                super.setItem(RESULT_SLOT, null);
            } else {
                super.setItem(RESULT_SLOT, recipe.getResult());
            }
        }
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
            for (int i = 0; i < 4; ++i) {
                setItem(i, matrix[i]);
            }
        }
    }

    public ItemStack getResult() {
        return getItem(RESULT_SLOT);
    }

    public ItemStack[] getMatrix() {
        return Arrays.copyOfRange(getContents(), MATRIX_START, getSize());
    }

    public void setResult(ItemStack newResult) {
        setItem(RESULT_SLOT, newResult);
    }

    public void setMatrix(ItemStack[] contents) {
        if (contents.length != 4) {
            throw new IllegalArgumentException("Length must be 4");
        }
        for (int i = 0; i < 4; ++i) {
            setItem(i + 1, contents[i]);
        }
    }

    public Recipe getRecipe() {
        return ((GlowServer) Bukkit.getServer()).getCraftingManager().getCraftingRecipe(getMatrix());
    }

    // Slot conversion

    private final static int slotConversion[] = {
            1, 2, 3, 4, 0
    };

}
