package net.glowstone.inventory;

import com.google.common.collect.Iterators;
import net.glowstone.GlowServer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

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
        int shape = shapedRecipes.size(), nshape = shapelessRecipes.size(), furnace = furnaceRecipes.size(), fuel = furnaceFuels.size();
        GlowServer.logger.log(Level.INFO, "Recipes: {0} shaped, {1} shapeless, {2} furnace, {3} fuels.", new Object[] { shape, nshape, furnace, fuel });
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
            if (recipe.getInput().getType() != input.getType()) {
                continue;
            } else if (recipe.getInput().getDurability() >= 0 && recipe.getInput().getDurability() != input.getDurability()) {
                continue;
            } else {
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
    public int getFuelTime(int material) {
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
        // TODO
    }
    
    /**
     * Get a shaped or shapeless recipe from the crafting manager.
     * @param items An array of items with null being empty slots. Length should be a perfect square.
     * @return The ShapedRecipe or ShapelessRecipe that matches the input, or null if none match.
     */
    public Recipe getCraftingRecipe(ItemStack[] items) {
        int size = (int) Math.sqrt(items.length);
        
        if (size != Math.sqrt(items.length)) {
            throw new IllegalArgumentException("ItemStack list was not square (was " + items.length + ")");
        }
        
        ItemStack[] reversedItems = new ItemStack[items.length];
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                int col2 = size - 1 - col;
                reversedItems[row * size + col] = items[row * size + col2];
            }
        }
        
        ShapedRecipe result = getShapedRecipe(size, items);
        if (result != null) {
            return result;
        }
        
        result = getShapedRecipe(size, reversedItems);
        if (result != null) {
            return result;
        }
        
        return getShapelessRecipe(items);
    }
    
    private ShapedRecipe getShapedRecipe(int size, ItemStack[] items) {
        for (ShapedRecipe recipe : shapedRecipes) {
            Map<Character, ItemStack> ingredients = recipe.getIngredientMap();
            String[] shape = recipe.getShape();
            
            int rows = shape.length, cols = 0;
            for (int row = 0; row < rows; ++row) {
                if (shape[row].length() > cols) {
                    cols = shape[row].length();
                }
            }
            
            if (rows == 0 || cols == 0) continue;
            
            for (int rStart = 0; rStart <= size - rows; ++rStart) {
                for (int cStart = 0; cStart <= size - cols; ++cStart) {
                    boolean failed = false;
                    boolean[] accountedFor = new boolean[items.length];
                    
                    for (int i = 0; i < items.length; ++i) {
                        accountedFor[i] = items[i] == null;
                    }
                    
                    for (int row = 0; row < rows; ++row) {
                        for (int col = 0; col < cols; ++col) {
                            ItemStack given = items[(rStart + row) * size + cStart + col];
                            char ingredientChar = shape[row].length() >= col - 1 ? shape[row].charAt(col) : ' ';
                            
                            if (given == null) {
                                if (ingredients.containsKey(ingredientChar)) {
                                    failed = true;
                                    break;
                                } else {
                                    accountedFor[(rStart + row) * size + cStart + col] = true;
                                }
                            } else if (ingredients.get(ingredientChar) == null) {
                                failed = true;
                                break;
                            } else {
                                MaterialData data = ingredients.get(ingredientChar).getData();
                                if (data.getItemType() != given.getType()) {
                                    failed = true;
                                    break;
                                } else if (data.getData() >= 0 && data.getData() != given.getDurability()) {
                                    failed = true;
                                    break;
                                } else {
                                    accountedFor[(rStart + row) * size + cStart + col] = true;
                                }
                            }
                        }
                        if (failed) {
                            break;
                        }
                    }
                    
                    for (int i = 0; i < accountedFor.length; ++i) {
                        if (!accountedFor[i]) {
                            failed = true;
                            break;
                        }
                    }
                    
                    if (!failed) {
                        return recipe;
                    }
                }
            }
        }
    
        return null;
    }
    
    private ShapelessRecipe getShapelessRecipe(ItemStack[] items) {
        for (ShapelessRecipe recipe : shapelessRecipes) {
            boolean failed = false;
            boolean[] accountedFor = new boolean[items.length];
            
            // Make sure each ingredient in the recipe exists in the inventory
            for (ItemStack stack : recipe.getIngredientList()) {
                MaterialData ingredient = stack.getData();
                boolean found = false;
                for (int i = 0; i < items.length; ++i) {
                    if (!accountedFor[i]) {
                        if (items[i] == null) {
                            accountedFor[i] = true;
                            continue;
                        } else if (ingredient.getItemType() != items[i].getType()) {
                            failed = true;
                            break;
                        } else if (ingredient.getData() >= 0 && ingredient.getData() != items[i].getDurability()) {
                            failed = true;
                            break;
                        } else {
                            found = true;
                            accountedFor[i] = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    failed = true;
                    break;
                }
            }
            
            // Make sure inventory has no leftover items
            for (int i = 0; i < items.length; ++i) {
                if (!accountedFor[i]) {
                    failed = true;
                    break;
                }
            }
            
            if (!failed) {
                return recipe;
            }
        }
        
        return null;
    }

    public Iterator<Recipe> iterator() {
        return Iterators.concat(shapedRecipes.iterator(), shapelessRecipes.iterator(), furnaceRecipes.iterator());
    }

    /**
     * Get a list of all recipes for a given item. The stack size is ignored
     * in comparisons. If the durability is -1, it will match any data value.
     *
     * @param result The item whose recipes you want
     * @return The list of recipes
     */
    public List<Recipe> getRecipesFor(ItemStack result) {
        List<Recipe> recipes = new LinkedList<>();
        for (Recipe recipe : this) {
            ItemStack stack = recipe.getResult();
            if (stack.getType() == result.getType() && (result.getDurability() == -1 || result.getDurability() == stack.getDurability())) {
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

    // -- Helper functions
    
    private ShapedRecipe makeShaped(Material mat) {
        return makeShaped(mat, 1, 0);
    }
    
    private ShapedRecipe makeShaped(Material mat, int amount) {
        return makeShaped(mat, amount, 0);
    }
    
    private ShapedRecipe makeShaped(Material mat, int amount, int data) {
        ShapedRecipe result = new ShapedRecipe(new ItemStack(mat, amount, (byte) data));
        addRecipe(result);
        return result;
    }
    
    private ShapelessRecipe makeShapeless(Material mat, int amount, int data) {
        ShapelessRecipe result = new ShapelessRecipe(new ItemStack(mat, amount, (byte) data));
        addRecipe(result);
        return result;
    }

    private enum CraftingSet {        
        WOOD(Material.WOOD,
                tools(Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_HOE, Material.WOOD_SWORD), stairs(2, Material.WOOD_STAIRS)),
        COBBLESTONE(Material.COBBLESTONE,
                tools(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_HOE, Material.STONE_SWORD), stairs(3, Material.COBBLESTONE_STAIRS)),
        GOLD(Material.GOLD_INGOT, block(Material.GOLD_BLOCK),
                tools(Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_HOE, Material.GOLD_SWORD),
                armor(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS)),
        IRON(Material.IRON_INGOT, block(Material.IRON_BLOCK),
                tools(Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_HOE, Material.IRON_SWORD),
                armor(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS)),
        DIAMOND(Material.DIAMOND, block(Material.DIAMOND_BLOCK),
                tools(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD),
                armor(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS)),
        LEATHER(Material.LEATHER,
                armor(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS)),
        CHAINMAIL(Material.FIRE,
               armor(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS)),
        BRICK(Material.BRICK, stairs(4, Material.BRICK_STAIRS)),
        SMOOTH_BRICK(Material.SMOOTH_BRICK, stairs(5, Material.SMOOTH_STAIRS)),
        SANDSTONE(Material.SANDSTONE, stairs(1, null)),
        STONE(Material.STONE, stairs(0, null));
        
        
        private final Material material;
        private Material block;

        private Material[] armor;
        private Material[] tools;
        private Material stairs;
        private int slabData = -1;
        
        private CraftingSet(Material mat, Property... props) {
            material = mat;
            for (Property p : props)
                p.apply(this);
        }
        
        public Material getInput() {
            return material;
        }
        
        public Material getBlock() {
            return block;
        }
        
        public Material[] getArmor() {
            return armor;
        }
        
        public Material[] getTools() {
            return tools;
        }

        public Material getStairMaterial() {
            return stairs;
        }

        public int getSlabData() {
            return slabData;
        }
        
        // -----------------
    
        private interface Property {
            void apply(CraftingSet set);
        }
        
        private static Property block(final Material mat) {
            return new Property() { public void apply(CraftingSet s) {
                s.block = mat;
            }};
        }
        
        private static Property tools(final Material axe, final Material pick, final Material shovel, final Material hoe, final Material sword) {
            return new Property() { public void apply(CraftingSet s) {
                s.tools = new Material[] { axe, pick, shovel, hoe, sword };
            }};
        }
        
        private static Property armor(final Material helmet, final Material chestplate, final Material leggings, final Material boots) {
            return new Property() { public void apply(CraftingSet s) {
                s.armor = new Material[] { helmet, chestplate, leggings, boots };
            }};
        }

        private static Property stairs(final int slabData, final Material stairs) {
            return new Property() { public void apply(CraftingSet set) {
                set.stairs = stairs;
                set.slabData = slabData;
            }};
        }
        
    }
    
}
