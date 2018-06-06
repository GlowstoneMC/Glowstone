package net.glowstone.inventory.crafting;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.glowstone.testutils.ServerShim;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.hamcrest.core.IsNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BasicCraftingTest {

    static CraftingManager cm;

    @BeforeClass
    public static void setupClass() {
        // do this @BeforeClass and not @Before since it's 10x as slow as some other test cases due to loading and parsing all the recipes
        ServerShim.install();

        cm = new CraftingManager();
        // loads recipes from .yml file, etc.
        cm.resetRecipes();
    }

    @Test
    public void can_craft_wood_from_logs() {
        /*
         * Used to "prove" the CraftingManager's recipe system loads and properly finds a simple matching recipe for some inputs.
         * Sometimes needed to rule out other issues.
         */
        ItemStack[] items = new ItemStack[4];
        items[0] = new ItemStack(Material.LOG, 1, (short) 0);
        Recipe recipe = cm.getCraftingRecipe(items);
        assertThat("Crafting manager did not get recipe", recipe, IsNull.notNullValue());
        assertThat("Crafting manager got wrong material", Material.WOOD,
            is(recipe.getResult().getType()));
        assertThat("Crafting manager got wrong amount", 4, is(recipe.getResult().getAmount()));
    }
}
