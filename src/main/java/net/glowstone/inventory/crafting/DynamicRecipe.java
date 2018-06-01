package net.glowstone.inventory.crafting;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * <p>Represents a dynamic recipe. These are recipes that have can have different results, depending
 * on the inputs used, rather than a simple matching algorithm.</p>
 *
 * <p>Used for recipes such as banners, which require item metadata to be copied to an item, along
 * with having a semi-shaped recipe.</p>
 */
public class DynamicRecipe implements Recipe {

    private ItemMatcher matcher;
    /**
     * Gets the result of this recipe, given the input of {@link #matches(ItemStack[])}.
     *
     * @return The result of the recipe, or null if it does not match
     */
    @Getter
    private ItemStack result;

    public DynamicRecipe() {
        setMatcher(new ItemMatcher());
    }

    public DynamicRecipe(ItemMatcher matcher) {
        setMatcher(matcher);
    }

    /**
     * Sets the {@link ItemMatcher} to be used with this recipe.
     *
     * @param matcher ItemMatcher to use. Must not be null.
     */
    public void setMatcher(ItemMatcher matcher) {
        Preconditions.checkNotNull(matcher, "Matcher cannot be null.");
        this.matcher = matcher;
    }

    /**
     * Checks to see if the recipe will match a crafting matrix. This method also prepares
     * {@link #getResult()} to return the correct item (including all metadata) for the input.
     *
     * @param matrix Items on the crafting grid
     * @return Whether the recipe matches the inputs
     */
    public boolean matches(ItemStack[] matrix) {
        result = matcher.getResult(matrix);
        return result != null;
    }
}
