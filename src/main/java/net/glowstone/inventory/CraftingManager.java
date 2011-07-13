package net.glowstone.inventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import net.glowstone.GlowServer;

/**
 * Manager for crafting and smelting recipes
 */
public final class CraftingManager {
    
    private final ArrayList<ShapedRecipe> shapedRecipes = new ArrayList<ShapedRecipe>();
    private final ArrayList<ShapelessRecipe> shapelessRecipes = new ArrayList<ShapelessRecipe>();
    private final ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<FurnaceRecipe>();
    private final EnumMap<Material, Integer> furnaceFuels = new EnumMap<Material, Integer>(Material.class);
    
    public CraftingManager() {
        resetRecipes();
        
        // Report stats
        int shape = shapedRecipes.size(), nshape = shapelessRecipes.size(), furnace = furnaceRecipes.size(), fuel = furnaceFuels.size();
        GlowServer.logger.log(Level.INFO, "Recipes: {0} shaped, {1} shapeless, {2} furnace, {3} fuels.", new Object[] { shape, nshape, furnace, fuel });
        
        testRecipe("workbench", new ItemStack(Material.WORKBENCH), s("WOOD"), s("WOOD"), s("WOOD"), s("WOOD"));
        testRecipe("iron", new ItemStack(Material.IRON_INGOT, 9), s("IRON_BLOCK"));
    }
    
    private ItemStack s(String s) {
        return new ItemStack(Material.getMaterial(s), 1);
    }
    
    /**
     * Tests a recipe to make sure it 
     */
    private void testRecipe(String name, ItemStack want, ItemStack... input) {
        Recipe recipe = getCraftingRecipe(input);
        if (recipe == null) {
            if (want == null) {
                System.out.println("Test " + name + ": OK [null]");
            } else {
                System.out.println("Test " + name + ": FAIL: want " + want + " got null");
            }
        } else if (want.equals(recipe.getResult())) {
            System.out.println("Test " + name + ": OK [" + want + "]");
        } else {
            String result = (recipe == null) ? null : recipe.getResult().toString();
            System.out.println("Test " + name + ": FAIL: want " + want + " got " + result);
        }
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
            if (recipe.getInput().equals(input.getData())) {
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
     * Get a shaped or shapeless recipe from the crafting manager.
     * @param items An array of items with null being empty slots. Length should be a perfect square.
     * @return The ShapedRecipe or ShapelessRecipe that matches the input, or null if none match.
     */
    public Recipe getCraftingRecipe(ItemStack[] items) {
        if (Math.sqrt(items.length) != (int)Math.sqrt(items.length)) {
            throw new IllegalArgumentException("ItemStack list was not square (was " + items.length + ")");
        }
        
        int size = (int) Math.sqrt(items.length);
        
        for (ShapedRecipe recipe : shapedRecipes) {
            HashMap<Character, MaterialData> ingredients = recipe.getIngredientMap();
            String[] shape = recipe.getShape();
            
            int rows = shape.length, cols = 0;
            for (int row = 0; row < rows; ++row) {
                if (shape[row].length() > cols) {
                    cols = shape[row].length();
                }
            }
            
            if (rows == 0 || cols == 0) continue;
            
            //System.out.println("Testing shaped recipe " + recipe.getResult());
            
            for (int rStart = 0; rStart <= size - rows; ++rStart) {
                for (int cStart = 0; cStart <= size - cols; ++cStart) {
                    //System.out.println("Checking start " + rStart + "," + cStart);
                    boolean failed = false;
                    boolean[] accountedFor = new boolean[items.length];
                    
                    for (int row = 0; row < rows; ++row) {
                        for (int col = 0; col < cols; ++col) {
                            ItemStack given = items[(rStart + row) * size + cStart + col];
                            char ingredientChar = shape[row].length() >= col - 1 ? shape[row].charAt(col) : ' ';
                            
                            //System.out.println("Checking " + row + "," + col + ": " + given + " " + ingredientChar);
                            
                            if (given == null) {
                                if (ingredientChar != ' ') {
                                    failed = true;
                                    break;
                                } else {
                                    accountedFor[(rStart + row) * size + cStart + col] = true;
                                }
                            } else if (ingredients.get(ingredientChar) == null) {
                                failed = true;
                                break;
                            } else {
                                MaterialData data = ingredients.get(ingredientChar);
                                //System.out.println("Checking " + given + " against " + data);
                                if (data.getItemType() != given.getType()) {
                                    //System.out.println("Type does not equal");
                                    failed = true;
                                    break;
                                } else if (data.getData() >= 0 && data.getData() != given.getDurability()) {
                                    //System.out.println("Data does not equal");
                                    failed = true;
                                    break;
                                } else {
                                    //System.out.println("Type and data equal");
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
        
        for (ShapelessRecipe recipe : shapelessRecipes) {
            boolean failed = false;
            boolean[] accountedFor = new boolean[items.length];
            
            // Make sure each ingredient in the recipe exists in the inventory
            for (MaterialData ingredient : recipe.getIngredientList()) {
                boolean found = false;
                for (int i = 0; i < items.length; ++i) {
                    if (!accountedFor[i]) {
                        if (ingredient.getItemType() != items[i].getType()) {
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
    
    /**
     * Reset the crafting recipe lists to their default states.
     */
    public void resetRecipes() {
        shapedRecipes.clear();
        shapelessRecipes.clear();
        furnaceRecipes.clear();
        furnaceFuels.clear();
        
        // Crafting sets
        for (CraftingSet set : CraftingSet.values()) {
            if (set.getBlock() != null) {
                addRecipe(new ShapedRecipe(new ItemStack(set.getBlock(), 1)).shape("xxx", "xxx", "xxx").setIngredient('x', set.getInput()));
                addRecipe(new ShapedRecipe(new ItemStack(set.getInput(), 9)).shape("x").setIngredient('x', set.getBlock()));
            }
            
            if (set.getArmor() != null) {
                String xxx = "xxx", x_x = "x x";
                addRecipe(new ShapedRecipe(new ItemStack(set.getArmor()[0], 1)).shape(xxx, x_x).setIngredient('x', set.getInput())); // helmet
                addRecipe(new ShapedRecipe(new ItemStack(set.getArmor()[1], 1)).shape(x_x, xxx, xxx).setIngredient('x', set.getInput())); // chestplate
                addRecipe(new ShapedRecipe(new ItemStack(set.getArmor()[2], 1)).shape(xxx, x_x, x_x).setIngredient('x', set.getInput())); // leggings
                addRecipe(new ShapedRecipe(new ItemStack(set.getArmor()[3], 1)).shape(x_x, x_x).setIngredient('x', set.getInput())); // boots
            }
            
            if (set.getTools() != null) {
                addRecipe(new ShapedRecipe(new ItemStack(set.getTools()[0], 1)).shape("xx","x|"," |").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK)); // axe
                addRecipe(new ShapedRecipe(new ItemStack(set.getTools()[1], 1)).shape("xxx"," | "," | ").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK)); // pick
                addRecipe(new ShapedRecipe(new ItemStack(set.getTools()[2], 1)).shape("x","|","|").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK)); // shovel
                addRecipe(new ShapedRecipe(new ItemStack(set.getTools()[3], 1)).shape("xx"," |"," |").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK)); // hoe
                addRecipe(new ShapedRecipe(new ItemStack(set.getTools()[4], 1)).shape("x","x","|").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK)); // sword
            }
        }
        
        // Basic Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD, 4)).shape("l").setIngredient('l', Material.LOG, -1)); // Wooden Planks
        addRecipe(new ShapedRecipe(new ItemStack(Material.STICK, 4)).shape("w", "w").setIngredient('w', Material.WOOD)); // Sticks
        addRecipe(new ShapedRecipe(new ItemStack(Material.TORCH, 4)).shape("c", "s").setIngredient('c', Material.COAL, -1).setIngredient('s', Material.STICK)); // Torches
        addRecipe(new ShapedRecipe(new ItemStack(Material.WORKBENCH)).shape("ww", "ww").setIngredient('w', Material.WOOD)); // Workbench
        addRecipe(new ShapedRecipe(new ItemStack(Material.FURNACE)).shape("ccc", "c c", "ccc").setIngredient('c', Material.COBBLESTONE)); // Furnace
        addRecipe(new ShapedRecipe(new ItemStack(Material.CHEST)).shape("www","w w","www").setIngredient('w', Material.WOOD)); // Chest
        
        // Block Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.LAPIS_BLOCK)).shape("xxx","xxx","xxx").setIngredient('x', Material.INK_SACK, 4)); // Lapis
        addRecipe(new ShapedRecipe(new ItemStack(Material.INK_SACK, 9, (byte) 4)).shape("xxx","xxx","xxx").setIngredient('x', Material.LAPIS_BLOCK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.GLOWSTONE)).shape("xx","xx").setIngredient('x', Material.GLOWSTONE_DUST));
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOL)).shape("xx","xx").setIngredient('x', Material.STRING));
        addRecipe(new ShapedRecipe(new ItemStack(Material.TNT)).shape("-o-","o-o","-o-").setIngredient('-', Material.SULPHUR).setIngredient('o', Material.SAND)); // TNT
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 3, (byte)0)).shape("xxx").setIngredient('x', Material.STONE)); // Slab #0
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 3, (byte)1)).shape("xxx").setIngredient('x', Material.SANDSTONE)); // Slab #1
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 3, (byte)2)).shape("xxx").setIngredient('x', Material.WOOD)); // Slab #2
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 3, (byte)3)).shape("xxx").setIngredient('x', Material.COBBLESTONE)); // Slab #3
        addRecipe(new ShapedRecipe(new ItemStack(Material.COBBLESTONE_STAIRS, 4)).shape("x  ","xx ","xxx").setIngredient('x', Material.COBBLESTONE)); // Stairs one
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD_STAIRS, 4)).shape("x  ","xx ","xxx").setIngredient('x', Material.WOOD)); // Stairs two
        addRecipe(new ShapedRecipe(new ItemStack(Material.SNOW_BLOCK)).shape("xx","xx").setIngredient('x', Material.SNOW_BALL));
        addRecipe(new ShapedRecipe(new ItemStack(Material.CLAY)).shape("xx","xx").setIngredient('x', Material.CLAY_BALL));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BRICK)).shape("xx","xx").setIngredient('x', Material.CLAY_BRICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOOKSHELF)).shape("www","bbb","www").setIngredient('w', Material.WOOD).setIngredient('b', Material.BOOK)); // Bookshelf
        addRecipe(new ShapedRecipe(new ItemStack(Material.SANDSTONE)).shape("xx","xx").setIngredient('x', Material.SAND));
        addRecipe(new ShapedRecipe(new ItemStack(Material.JACK_O_LANTERN)).shape("p","t").setIngredient('p', Material.PUMPKIN).setIngredient('t', Material.TORCH)); // Jack-o-lantern
        
        // Tool Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.FLINT_AND_STEEL)).shape("i "," f").setIngredient('i', Material.IRON_INGOT).setIngredient('f', Material.FLINT)); // Flint and steel
        addRecipe(new ShapedRecipe(new ItemStack(Material.BUCKET)).shape("i i"," i ").setIngredient('i', Material.IRON_INGOT)); // Bucket
        addRecipe(new ShapedRecipe(new ItemStack(Material.COMPASS)).shape(" i ","iri"," i ").setIngredient('i', Material.IRON_INGOT).setIngredient('r', Material.REDSTONE)); // Compass
        addRecipe(new ShapedRecipe(new ItemStack(Material.MAP)).shape("ppp","pcp","ppp").setIngredient('p', Material.PAPER).setIngredient('c', Material.COMPASS)); // Map
        addRecipe(new ShapedRecipe(new ItemStack(Material.WATCH)).shape(" i ","iri"," i ").setIngredient('i', Material.GOLD_INGOT).setIngredient('r', Material.REDSTONE)); // Watch
        addRecipe(new ShapedRecipe(new ItemStack(Material.FISHING_ROD)).shape("  |"," |s","| s").setIngredient('|', Material.STICK).setIngredient('s', Material.STRING)); // Fishing rod
        addRecipe(new ShapedRecipe(new ItemStack(Material.SHEARS)).shape(" i","i ").setIngredient('i', Material.IRON_INGOT)); // Shears
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOW)).shape(" |s","| s"," |s").setIngredient('|', Material.STICK).setIngredient('s', Material.STRING)); // Bow
        addRecipe(new ShapedRecipe(new ItemStack(Material.ARROW, 4)).shape("^","|","f").setIngredient('^', Material.FLINT).setIngredient('|', Material.STICK).setIngredient('f', Material.FEATHER)); // Arrow
        
        // Transportation Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.MINECART)).shape("i i","iii").setIngredient('i', Material.IRON_INGOT));
        addRecipe(new ShapedRecipe(new ItemStack(Material.POWERED_MINECART)).shape("f","m").setIngredient('f', Material.FURNACE).setIngredient('m', Material.MINECART));
        addRecipe(new ShapedRecipe(new ItemStack(Material.STORAGE_MINECART)).shape("c","m").setIngredient('c', Material.CHEST).setIngredient('m', Material.MINECART));
        addRecipe(new ShapedRecipe(new ItemStack(Material.RAILS, 16)).shape("i i","isi","i i").setIngredient('i', Material.IRON_INGOT).setIngredient('s', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.POWERED_RAIL, 6)).shape("g g","gsg","grg").setIngredient('g', Material.GOLD_INGOT).setIngredient('s', Material.STICK).setIngredient('r', Material.REDSTONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.DETECTOR_RAIL, 6)).shape("i i","ipi","iri").setIngredient('i', Material.IRON_INGOT).setIngredient('p', Material.STONE_PLATE).setIngredient('r', Material.REDSTONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOAT)).shape("w w","www").setIngredient('w', Material.WOOD));
        
        // Mechanism Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD_DOOR)).shape("ww","ww","ww").setIngredient('w', Material.WOOD));
        addRecipe(new ShapedRecipe(new ItemStack(Material.IRON_DOOR)).shape("ii","ii","ii").setIngredient('i', Material.IRON_INGOT));
        addRecipe(new ShapedRecipe(new ItemStack(Material.TRAP_DOOR, 2)).shape("www","www").setIngredient('w', Material.WOOD));
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD_PLATE)).shape("ww").setIngredient('w', Material.WOOD));
        addRecipe(new ShapedRecipe(new ItemStack(Material.STONE_PLATE)).shape("ss").setIngredient('s', Material.STONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.STONE_BUTTON)).shape("s","s").setIngredient('s', Material.STONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.REDSTONE_TORCH_ON)).shape("r","/").setIngredient('r', Material.REDSTONE).setIngredient('/', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.LEVER)).shape("/","s").setIngredient('s', Material.COBBLESTONE).setIngredient('/', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.NOTE_BLOCK)).shape("www","wrw","www").setIngredient('w', Material.WOOD).setIngredient('r', Material.REDSTONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.JUKEBOX)).shape("www","wdw","www").setIngredient('w', Material.WOOD).setIngredient('d', Material.DIAMOND));
        addRecipe(new ShapedRecipe(new ItemStack(Material.DISPENSER)).shape("ccc", "cbc", "crc").setIngredient('c', Material.COBBLESTONE).setIngredient('b', Material.BOW).setIngredient('r', Material.REDSTONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.DIODE)).shape("trt","sss").setIngredient('t', Material.REDSTONE_TORCH_ON).setIngredient('r', Material.REDSTONE).setIngredient('s', Material.STONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.PISTON_BASE)).shape("www","sis","srs").setIngredient('w', Material.WOOD).setIngredient('s', Material.COBBLESTONE).setIngredient('i', Material.IRON_INGOT).setIngredient('r', Material.REDSTONE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.PISTON_STICKY_BASE)).shape("s","p").setIngredient('s', Material.SLIME_BALL).setIngredient('p', Material.PISTON_BASE));
        
        // Food Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOWL, 4)).shape("w w"," w ").setIngredient('w', Material.WOOD));
        addRecipe(new ShapedRecipe(new ItemStack(Material.MUSHROOM_SOUP)).shape("r","b","u").setIngredient('r', Material.RED_MUSHROOM).setIngredient('b', Material.BROWN_MUSHROOM).setIngredient('u', Material.BOWL));
        addRecipe(new ShapedRecipe(new ItemStack(Material.MUSHROOM_SOUP)).shape("b","r","u").setIngredient('r', Material.RED_MUSHROOM).setIngredient('b', Material.BROWN_MUSHROOM).setIngredient('u', Material.BOWL));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BREAD)).shape("www").setIngredient('w', Material.WHEAT));
        addRecipe(new ShapedRecipe(new ItemStack(Material.SUGAR)).shape("s").setIngredient('s', Material.SUGAR_CANE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.CAKE)).shape("mmm","ses","www").setIngredient('m', Material.MILK_BUCKET).setIngredient('s', Material.SUGAR).setIngredient('e', Material.EGG).setIngredient('w', Material.WHEAT));
        addRecipe(new ShapedRecipe(new ItemStack(Material.COOKIE, 8)).shape("wdw").setIngredient('w', Material.WHEAT).setIngredient('d', Material.INK_SACK, 3));
        addRecipe(new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE)).shape("ggg","gag","ggg").setIngredient('g', Material.GOLD_BLOCK).setIngredient('a', Material.APPLE));
        
        // Miscellaneous Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.PAINTING)).shape("///","/w/","///").setIngredient('/', Material.STICK).setIngredient('w', Material.WOOL));
        addRecipe(new ShapedRecipe(new ItemStack(Material.SIGN)).shape("www","www"," / ").setIngredient('w', Material.WOOD).setIngredient('/', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.LADDER, 2)).shape("/ /","///","/ /").setIngredient('/', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.PAPER, 3)).shape("sss").setIngredient('s', Material.SUGAR_CANE));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOOK)).shape("p","p","p").setIngredient('p', Material.PAPER));
        addRecipe(new ShapedRecipe(new ItemStack(Material.FENCE, 2)).shape("///","///").setIngredient('/', Material.STICK));
        addRecipe(new ShapedRecipe(new ItemStack(Material.BED)).shape("ccc","www").setIngredient('c', Material.WOOL).setIngredient('w', Material.WOOD)); 
        
        // Dye Recipes
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 1)).addIngredient(Material.RED_ROSE)); // Rose Red
        // 2 = cactus [smelt], 3 = cocoa [dungeon], 4 = lapis [mine]
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 5)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 1)); // Purple
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 6)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 2)); // Cyan
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 3, (byte) 7)).addIngredient(Material.INK_SACK).addIngredient(2, Material.INK_SACK, 15)); // Light gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 7)).addIngredient(Material.INK_SACK, 8).addIngredient(Material.INK_SACK, 15)); // Light gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 8)).addIngredient(Material.INK_SACK).addIngredient(Material.INK_SACK, 15)); // Gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 9)).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 15)); // Pink
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 10)).addIngredient(Material.INK_SACK, 2).addIngredient(Material.INK_SACK, 15)); // Lime
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 11)).addIngredient(Material.YELLOW_FLOWER)); // Dandelion Yellow
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 12)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15)); // Light blue
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 13)).addIngredient(Material.INK_SACK, 5).addIngredient(Material.INK_SACK, 9)); // Magenta
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 3, (byte) 13)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 9).addIngredient(Material.INK_SACK, 1)); // Magenta
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 4, (byte) 13)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15).addIngredient(2, Material.INK_SACK, 1)); // Magenta
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 2, (byte) 14)).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 11)); // Orange
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 3, (byte) 15)).addIngredient(Material.BONE)); // Bonemeal
        
        
        // Wool Recipies
        for (int i = 1; i < 16; ++i) {
            // Note: data for wool and dye is inverse, hence 15-i
            addRecipe(new ShapelessRecipe(new ItemStack(Material.WOOL, 1, (byte) i)).addIngredient(Material.WOOL).addIngredient(Material.INK_SACK, (byte)(15 - i)));
        }
        
        // Smelting Recipes
        addRecipe(new FurnaceRecipe(new ItemStack(Material.IRON_INGOT), Material.IRON_ORE));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.GOLD_INGOT), Material.GOLD_ORE));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.GLASS), Material.SAND));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.STONE), Material.COBBLESTONE));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.GRILLED_PORK), Material.PORK));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.CLAY_BRICK), Material.CLAY));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.COOKED_FISH), Material.RAW_FISH));
        addRecipe(new FurnaceRecipe(new ItemStack(Material.COAL, 1, (byte)1), Material.LOG)); // Charcoal
        addRecipe(new FurnaceRecipe(new ItemStack(Material.INK_SACK, 1, (byte)2), Material.CACTUS)); // Cactus green
        addRecipe(new FurnaceRecipe(new ItemStack(Material.DIAMOND), Material.DIAMOND_ORE));
        
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
    
    private enum CraftingSet {        
        WOOD(Material.WOOD,
                tools(Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_HOE, Material.WOOD_SWORD)),
        STONE(Material.COBBLESTONE,
                tools(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_HOE, Material.STONE_SWORD)),
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
                armor(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));
        
        
        private Material material;
        private Material block;
        
        private Material[] armor;
        private Material[] tools;
        
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
        
    }
    
}
