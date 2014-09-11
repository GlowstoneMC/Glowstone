package net.glowstone.inventory;

import com.google.common.collect.Iterators;
import net.glowstone.GlowServer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.InputStream;
import java.util.*;

/**
 * Manager for crafting and smelting recipes
 */
public final class CraftingManager implements Iterable<Recipe> {

    private final ArrayList<ShapedRecipe> shapedRecipes = new ArrayList<>();
    private final ArrayList<ShapelessRecipe> shapelessRecipes = new ArrayList<>();
    private final ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<>();
    private final Map<Material, Integer> furnaceFuels = new HashMap<>();

    public void initialize() {
        resetRecipes();

        // Report stats
        GlowServer.logger.info("Recipes: " +
                shapedRecipes.size() + " shaped, " +
                shapelessRecipes.size() + " shapeless, " +
                furnaceRecipes.size() + " furnace, " +
                furnaceFuels.size() + " fuels.");
    }

    /**
     * Adds a recipe to the crafting manager.
     * @param recipe The recipe to add.
     * @return Whether adding the recipe was successful.
     */
    public boolean addRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            shapedRecipes.add((ShapedRecipe) recipe);
            return true;
        } else if (recipe instanceof ShapelessRecipe) {
            shapelessRecipes.add((ShapelessRecipe) recipe);
            return true;
        } else if (recipe instanceof FurnaceRecipe) {
            furnaceRecipes.add((FurnaceRecipe) recipe);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get a furnace recipe from the crafting manager.
     * @param input The furnace input.
     * @return The FurnaceRecipe, or null if none is found.
     */
    public FurnaceRecipe getFurnaceRecipe(ItemStack input) {
        for (FurnaceRecipe recipe : furnaceRecipes) {
            if (matchesWildcard(recipe.getInput(), input)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Get how long a given fuel material will burn for.
     * @param material The fuel material.
     * @return The time in ticks, or 0 if that material does not burn.
     */
    public int getFuelTime(Material material) {
        if (furnaceFuels.containsKey(material)) {
            return furnaceFuels.get(material);
        } else {
            return 0;
        }
    }

    /**
     * Remove enough items from the given item list to form the given recipe.
     * @param items The items to remove the ingredients from.
     * @param recipe A recipe known to match the items.
     */
    public void removeItems(ItemStack[] items, Recipe recipe) {
        // todo
    }

    /**
     * Get a shaped or shapeless recipe from the crafting manager.
     * @param items An array of items with null being empty slots. Length should be a perfect square.
     * @return The ShapedRecipe or ShapelessRecipe that matches the input, or null if none match.
     */
    public Recipe getCraftingRecipe(ItemStack[] items) {
        int size = (int) Math.sqrt(items.length);

        if (size * size != items.length) {
            throw new IllegalArgumentException("ItemStack list was not square (was " + items.length + ")");
        }

        ShapedRecipe result = getShapedRecipe(size, items);
        if (result != null) {
            return result;
        }

        ItemStack[] reversedItems = new ItemStack[items.length];
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                int col2 = size - 1 - col;
                reversedItems[row * size + col] = items[row * size + col2];
            }
        }

        // this check saves the trouble of iterating through all the recipes again
        if (!Arrays.equals(items, reversedItems)) {
            result = getShapedRecipe(size, reversedItems);
            if (result != null) {
                return result;
            }
        }

        return getShapelessRecipe(items);
    }

    private ShapedRecipe getShapedRecipe(int size, ItemStack[] items) {
        for (ShapedRecipe recipe : shapedRecipes) {
            Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
            String[] shape = recipe.getShape();

            int rows = shape.length, cols = 0;
            for (String row : shape) {
                if (row.length() > cols) {
                    cols = row.length();
                }
            }

            if (rows == 0 || cols == 0) continue;

            // outer loop: try at each possible starting position
            position:
            for (int rStart = 0; rStart <= size - rows; ++rStart) {
                for (int cStart = 0; cStart <= size - cols; ++cStart) {
                    // inner loop: verify recipe against this position
                    for (int row = 0; row < rows; ++row) {
                        for (int col = 0; col < cols; ++col) {
                            ItemStack given = items[(rStart + row) * size + cStart + col];
                            char ingredientChar = shape[row].length() >= col - 1 ? shape[row].charAt(col) : ' ';
                            ItemStack expected = ingredients.get(ingredientChar);

                            // check for mismatch in presence of an item in that slot at all
                            if (expected == null) {
                                if (given != null) {
                                    continue position;
                                } else {
                                    continue; // good match
                                }
                            } else if (given == null) {
                                continue position;
                            }

                            // check for type and data match
                            if (!matchesWildcard(expected, given)) {
                                continue position;
                            }
                        }
                    }
                    // no mismatches at this position
                    return recipe;
                }
            } // end position loop
        } // end recipe loop

        return null;
    }

    private ShapelessRecipe getShapelessRecipe(ItemStack[] items) {
        recipe:
        for (ShapelessRecipe recipe : shapelessRecipes) {
            boolean[] accountedFor = new boolean[items.length];

            // Mark empty item slots accounted for
            for (int i = 0; i < items.length; ++i) {
                accountedFor[i] = items[i] == null;
            }

            // Make sure each ingredient in the recipe exists in the inventory
            ingredient:
            for (ItemStack ingredient : recipe.getIngredientList()) {
                for (int i = 0; i < items.length; ++i) {
                    // if this item is not already used and it matches this ingredient...
                    if (!accountedFor[i] && matchesWildcard(ingredient, items[i])) {
                        // ... this item is accounted for and this ingredient is found.
                        accountedFor[i] = true;
                        continue ingredient;
                    }
                }
                // no item matched this ingredient, so the recipe fails
                continue recipe;
            }

            // Make sure inventory has no leftover items
            for (int i = 0; i < items.length; ++i) {
                if (!accountedFor[i]) {
                    continue recipe;
                }
            }

            return recipe;
        }

        return null;
    }

    @Override
    public Iterator<Recipe> iterator() {
        return Iterators.concat(shapedRecipes.iterator(), shapelessRecipes.iterator(), furnaceRecipes.iterator());
    }

    private boolean isWildcard(short data) {
        // old-style wildcards (byte -1) not supported
        return data == Short.MAX_VALUE;
    }

    private boolean matchesWildcard(ItemStack expected, ItemStack actual) {
        return expected.getType() == actual.getType() && (isWildcard(expected.getDurability()) || expected.getDurability() == actual.getDurability());
    }

    /**
     * Get a list of all recipes for a given item. The stack size is ignored
     * in comparisons. If the durability is -1, it will match any data value.
     * @param result The item whose recipes you want
     * @return The list of recipes
     */
    public List<Recipe> getRecipesFor(ItemStack result) {
        // handling for old-style wildcards
        if (result.getDurability() == -1) {
            result = result.clone();
            result.setDurability(Short.MAX_VALUE);
        }

        List<Recipe> recipes = new LinkedList<>();
        for (Recipe recipe : this) {
            if (matchesWildcard(result, recipe.getResult())) {
                recipes.add(recipe);
            }
        }
        return recipes;
    }

    /**
     * Clear all recipes.
     */
    public void clearRecipes() {
        shapedRecipes.clear();
        shapelessRecipes.clear();
        furnaceRecipes.clear();
        furnaceFuels.clear();
    }

    /**
     * Reset the crafting recipe lists to their default states.
     */
    public void resetRecipes() {
        clearRecipes();
        loadRecipes();

        // Smelting fuels (time is in ticks)
        furnaceFuels.put(Material.COAL, 1600);
        furnaceFuels.put(Material.WOOD, 300);
        furnaceFuels.put(Material.SAPLING, 100);
        furnaceFuels.put(Material.STICK, 100);
        furnaceFuels.put(Material.FENCE, 300);
        furnaceFuels.put(Material.WOOD_STAIRS, 400);
        furnaceFuels.put(Material.TRAP_DOOR, 300);
        furnaceFuels.put(Material.LOG, 300);
        furnaceFuels.put(Material.WORKBENCH, 300);
        furnaceFuels.put(Material.BOOKSHELF, 300);
        furnaceFuels.put(Material.CHEST, 300);
        furnaceFuels.put(Material.JUKEBOX, 300);
        furnaceFuels.put(Material.NOTE_BLOCK, 300);
        furnaceFuels.put(Material.LOCKED_CHEST, 300);
        furnaceFuels.put(Material.LAVA_BUCKET, 20000);
    }

    /**
     * Load default recipes from built-in recipes.yml file.
     */
    @SuppressWarnings("unchecked")
    private void loadRecipes() {
        // Load recipes from recipes.yml file
        InputStream in = getClass().getClassLoader().getResourceAsStream("builtin/recipes.yml");
        if (in == null) {
            GlowServer.logger.warning("Could not find default recipes on classpath");
            return;
        }

        ConfigurationSection config = YamlConfiguration.loadConfiguration(in);

        // shaped
        for (Map<?, ?> data : config.getMapList("shaped")) {
            ItemStack resultStack = ItemStack.deserialize((Map<String, Object>) data.get("result"));
            ShapedRecipe recipe = new ShapedRecipe(resultStack);
            List<String> shape = (List<String>) data.get("shape");
            recipe.shape(shape.toArray(new String[shape.size()]));

            Map<String, Map<String, Object>> ingreds = (Map<String, Map<String, Object>>) data.get("ingredients");
            for (Map.Entry<String, Map<String, Object>> entry : ingreds.entrySet()) {
                ItemStack stack = ItemStack.deserialize(entry.getValue());
                recipe.setIngredient(entry.getKey().charAt(0), stack.getData());
            }

            shapedRecipes.add(recipe);
        }

        // shapeless
        for (Map<?, ?> data : config.getMapList("shapeless")) {
            ItemStack resultStack = ItemStack.deserialize((Map<String, Object>) data.get("result"));
            ShapelessRecipe recipe = new ShapelessRecipe(resultStack);

            List<Map<String, Object>> ingreds = (List<Map<String, Object>>) data.get("ingredients");
            for (Map<String, Object> entry : ingreds) {
                recipe.addIngredient(ItemStack.deserialize(entry).getData());
            }

            shapelessRecipes.add(recipe);
        }

        // furnace
        for (Map<?, ?> data : config.getMapList("furnace")) {
            ItemStack inputStack = ItemStack.deserialize((Map<String, Object>) data.get("input"));
            ItemStack resultStack = ItemStack.deserialize((Map<String, Object>) data.get("result"));
            furnaceRecipes.add(new FurnaceRecipe(resultStack, inputStack.getData()));
        }
    }

}
