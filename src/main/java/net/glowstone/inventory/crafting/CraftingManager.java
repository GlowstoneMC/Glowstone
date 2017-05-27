package net.glowstone.inventory.crafting;

import com.google.common.collect.Iterators;
import net.glowstone.GlowServer;
import net.glowstone.inventory.GlowCraftingInventory;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

/**
 * Manager for crafting and smelting recipes
 */
public final class CraftingManager implements Iterable<Recipe> {

    private final ArrayList<ShapedRecipe> shapedRecipes = new ArrayList<>();
    private final ArrayList<ShapelessRecipe> shapelessRecipes = new ArrayList<>();
    private final ArrayList<DynamicRecipe> dynamicRecipes = new ArrayList<>();
    private final ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<>();
    private final Map<Material, Integer> furnaceFuels = new HashMap<>();

    public void initialize() {
        resetRecipes();

        // Report stats
        GlowServer.logger.info("Recipes: " +
                shapedRecipes.size() + " shaped, " +
                shapelessRecipes.size() + " shapeless, " +
                furnaceRecipes.size() + " furnace, " +
                dynamicRecipes.size() + " dynamic, " +
                furnaceFuels.size() + " fuels.");
    }

    /**
     * Adds a recipe to the crafting manager.
     *
     * @param recipe The recipe to add.
     * @return Whether adding the recipe was successful.
     */
    public boolean addRecipe(Recipe recipe) {
        if (recipe instanceof ShapedRecipe) {
            return shapedRecipes.add((ShapedRecipe) recipe);
        }
        if (recipe instanceof ShapelessRecipe) {
            return shapelessRecipes.add((ShapelessRecipe) recipe);
        }
        if (recipe instanceof DynamicRecipe) {
            return dynamicRecipes.add((DynamicRecipe) recipe);
        }
        return recipe instanceof FurnaceRecipe && furnaceRecipes.add((FurnaceRecipe) recipe);
    }

    /**
     * Get a furnace recipe from the crafting manager.
     *
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
     *
     * @param material The fuel material.
     * @return The time in ticks, or 0 if that material does not burn.
     */
    public int getFuelTime(Material material) {
        return furnaceFuels.getOrDefault(material, 0);
    }

    public boolean isFuel(Material material) {
        return furnaceFuels.containsKey(material);
    }

    /**
     * Remove a layer of items from the crafting matrix and recipe result.
     *
     * @param items The items to remove the ingredients from.
     * @param inv   The inventory to remove the items from.
     */
    public void removeItems(ItemStack[] items, GlowCraftingInventory inv) {
        for (int i = 0; i < items.length; i++) {
            if (!InventoryUtil.isEmpty(items[i])) {
                int amount = items[i].getAmount();
                if (amount > 1) {
                    items[i].setAmount(amount - 1);
                } else {
                    inv.setItem(i + 1, InventoryUtil.createEmptyStack());
                }
            }
        }
    }

    /**
     * Get the amount of layers in the crafting matrix.
     * This assumes all Minecraft recipes have an item stack of 1 for all items in the recipe.
     *
     * @param items The items in the crafting matrix.
     * @return The number of stacks for a recipe.
     */
    public static int getLayers(ItemStack... items) {
        int layers = 0;
        for (ItemStack item : items) {
            if (item != null && (item.getAmount() < layers || layers == 0)) {
                layers = item.getAmount();
            }
        }
        return layers;
    }

    /**
     * Get a crafting recipe from the crafting manager.
     *
     * @param items An array of items with null being empty slots. Length should be a perfect square.
     * @return The Recipe that matches the input, or null if none match.
     */
    public Recipe getCraftingRecipe(ItemStack... items) {
        for (int i = 0; i < items.length; i++) {
            if (InventoryUtil.isEmpty(items[i])) {
                // TODO: rewrite recipe matcher to respect empty items
                items[i] = null;
            }
        }


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

        for (DynamicRecipe dynamicRecipe : dynamicRecipes) {
            if (dynamicRecipe.matches(items)) {
                return dynamicRecipe;
            }
        }

        return getShapelessRecipe(items);
    }

    private ShapedRecipe getShapedRecipe(int size, ItemStack... items) {
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
            for (int rStart = 0; rStart <= size - rows; ++rStart) {
                position:
                for (int cStart = 0; cStart <= size - cols; ++cStart) {
                    // inner loop: verify recipe against this position
                    for (int row = 0; row < rows; ++row) {
                        for (int col = 0; col < cols; ++col) {
                            ItemStack given = items[(rStart + row) * size + cStart + col];
                            char ingredientChar = shape[row].length() > col ? shape[row].charAt(col) : ' ';
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

                    // also check that no items outside the recipe size are present
                    for (int row = 0; row < size; row++) {
                        for (int col = 0; col < size; col++) {
                            // if this position is outside the recipe and non-null, fail
                            if ((row < rStart || row >= rStart + rows || col < cStart || col >= cStart + cols) &&
                                    items[row * size + col] != null) {
                                continue position;
                            }
                        }
                    }

                    // recipe matches and zero items outside the recipe part.
                    return recipe;
                }
            } // end position loop
        } // end recipe loop

        return null;
    }

    private ShapelessRecipe getShapelessRecipe(ItemStack... items) {
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
        return Iterators.concat(shapedRecipes.iterator(), shapelessRecipes.iterator(), dynamicRecipes.iterator(), furnaceRecipes.iterator());
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
     *
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

    public Recipe getRecipeByKey(NamespacedKey key) {
        for (ShapedRecipe shapedRecipe : shapedRecipes) {
            if (shapedRecipe.getKey().equals(key)) {
                return shapedRecipe;
            }
        }
        for (ShapelessRecipe shapelessRecipe : shapelessRecipes) {
            if (shapelessRecipe.getKey().equals(key)) {
                return shapelessRecipe;
            }
        }
        return null;
    }

    /**
     * Clear all recipes.
     */
    public void clearRecipes() {
        shapedRecipes.clear();
        shapelessRecipes.clear();
        dynamicRecipes.clear();
        furnaceRecipes.clear();
        furnaceFuels.clear();
    }

    /**
     * Reset the crafting recipe lists to their default states.
     */
    public void resetRecipes() {
        clearRecipes();
        loadRecipes();

        // Dynamic recipes
        dynamicRecipes.add(new DynamicRecipe(new GlowBannerMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowBannerCopyMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowRepairMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowArmorDyeMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowChargeMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowChargeFadeMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowFireworkMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowBookCopyMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowMapCopyMatcher()));
        dynamicRecipes.add(new DynamicRecipe(new GlowMapZoomMatcher()));

        // Smelting fuels (time is in ticks)
        furnaceFuels.put(Material.COAL, 1600);
        furnaceFuels.put(Material.WOOD, 300);
        furnaceFuels.put(Material.SAPLING, 100);
        furnaceFuels.put(Material.STICK, 100);
        furnaceFuels.put(Material.FENCE, 300);
        furnaceFuels.put(Material.WOOD_STAIRS, 300);
        furnaceFuels.put(Material.TRAP_DOOR, 300);
        furnaceFuels.put(Material.LOG, 300);
        furnaceFuels.put(Material.WORKBENCH, 300);
        furnaceFuels.put(Material.BOOKSHELF, 300);
        furnaceFuels.put(Material.CHEST, 300);
        furnaceFuels.put(Material.JUKEBOX, 300);
        furnaceFuels.put(Material.NOTE_BLOCK, 300);
        furnaceFuels.put(Material.LAVA_BUCKET, 20000);
        furnaceFuels.put(Material.COAL_BLOCK, 16000);
        furnaceFuels.put(Material.BLAZE_ROD, 2400);
        furnaceFuels.put(Material.WOOD_PLATE, 300);
        furnaceFuels.put(Material.FENCE_GATE, 300);
        furnaceFuels.put(Material.TRAPPED_CHEST, 300);
        furnaceFuels.put(Material.DAYLIGHT_DETECTOR, 300);
        furnaceFuels.put(Material.DAYLIGHT_DETECTOR_INVERTED, 300);
        furnaceFuels.put(Material.BANNER, 300);
        furnaceFuels.put(Material.WOOD_AXE, 200);
        furnaceFuels.put(Material.WOOD_HOE, 200);
        furnaceFuels.put(Material.WOOD_PICKAXE, 200);
        furnaceFuels.put(Material.WOOD_SPADE, 200);
        furnaceFuels.put(Material.WOOD_SWORD, 200);
        furnaceFuels.put(Material.WOOD_STEP, 150);
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

        ConfigurationSection config = YamlConfiguration.loadConfiguration(new InputStreamReader(in));

        // shaped
        for (Map<?, ?> data : config.getMapList("shaped")) {
            ItemStack resultStack = ItemStack.deserialize((Map<String, Object>) data.get("result"));
            ShapedRecipe recipe = new ShapedRecipe(resultStack);
            List<String> shape = (List<String>) data.get("shape");
            recipe.shape(shape.toArray(new String[shape.size()]));

            Map<String, Map<String, Object>> ingredients = (Map<String, Map<String, Object>>) data.get("ingredients");
            for (Entry<String, Map<String, Object>> entry : ingredients.entrySet()) {
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
            float xp = ((Number) data.get("xp")).floatValue();
            furnaceRecipes.add(new FurnaceRecipe(resultStack, inputStack.getType(), inputStack.getDurability(), xp));
        }
    }

}
