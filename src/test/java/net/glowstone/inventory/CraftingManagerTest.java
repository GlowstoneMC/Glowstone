package net.glowstone.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author zml2008
 */
public class CraftingManagerTest {
    private final CraftingManager craftManager = new CraftingManager();

    @Test
    public void testRecipes() {
        testRecipe("workbench", new ItemStack(Material.WORKBENCH, 1), s("WOOD"), s("WOOD"), s("WOOD"), s("WOOD"));
        testRecipe("iron", new ItemStack(Material.IRON_INGOT, 9), s("IRON_BLOCK"));
        testRecipe("wood", new ItemStack(Material.WOOD, 4), s("LOG"));
        testRecipe("wood 2", new ItemStack(Material.WOOD, 4), null, s("LOG"), null, null);
    }

    private ItemStack s(String s) {
        return new ItemStack(Material.getMaterial(s), 1);
    }

    /**
     * Tests a recipe to make sure it
     */
    private void testRecipe(String name, ItemStack want, ItemStack... input) {
        Recipe recipe = craftManager.getCraftingRecipe(input);
        if (recipe == null) {
            assertNotNull("Want " + want + " got null for " + name + " recipe", want);
        }  else {
            assertEquals("Want " + want + " got " + recipe.getResult().toString() + "for " + name + " recipe", want, recipe.getResult());
        }
    }
}
