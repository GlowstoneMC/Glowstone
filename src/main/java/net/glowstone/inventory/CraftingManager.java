package net.glowstone.inventory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import net.glowstone.block.BlockID;
import net.glowstone.block.ItemID;
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
    private final Map<Integer, Integer> furnaceFuels = new HashMap<Integer, Integer>();
    
    public CraftingManager() {
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
            if (recipe.getInput().getItemTypeId() != input.getTypeId()) {
                continue;
            } else if (recipe.getInput().getData() >= 0 && recipe.getInput().getData() != input.getDurability()) {
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
            HashMap<Character, MaterialData> ingredients = recipe.getIngredientMap();
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
                                MaterialData data = ingredients.get(ingredientChar);
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
            for (MaterialData ingredient : recipe.getIngredientList()) {
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
                makeShaped(set.getBlock().getId(), 1).shape("xxx", "xxx", "xxx").setIngredient('x', set.getInput());
                makeShaped(set.getInput().getId(), 9).shape("x").setIngredient('x', set.getBlock());
            }
            
            if (set.getArmor() != null) {
                String xxx = "xxx", x_x = "x x";
                makeShaped(set.getArmor()[0]).shape(xxx, x_x).setIngredient('x', set.getInput()); // helmet
                makeShaped(set.getArmor()[1]).shape(x_x, xxx, xxx).setIngredient('x', set.getInput()); // chestplate
                makeShaped(set.getArmor()[2]).shape(xxx, x_x, x_x).setIngredient('x', set.getInput()); // leggings
                makeShaped(set.getArmor()[3]).shape(x_x, x_x).setIngredient('x', set.getInput()); // boots
            }
            
            if (set.getTools() != null) {
                makeShaped(set.getTools()[0]).shape("xx","x|"," |").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK); // axe
                makeShaped(set.getTools()[1]).shape("xxx"," | "," | ").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK); // pick
                makeShaped(set.getTools()[2]).shape("x","|","|").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK); // shovel
                makeShaped(set.getTools()[3]).shape("xx"," |"," |").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK); // hoe
                makeShaped(set.getTools()[4]).shape("x","x","|").setIngredient('x', set.getInput()).setIngredient('|', Material.STICK); // sword
            }
            
            if (set.getStairMaterial() != -1) {
                makeShaped(set.getStairMaterial(), 4).shape("x  ","xx ","xxx").setIngredient('x', set.getInput());
            }

            if (set.getSlabData() != -1) {
                makeShaped(BlockID.STEP, 3, set.getSlabData()).shape("xxx").setIngredient('x', set.getInput());
            }
        }
        
        // Basic Recipes
        makeShaped(BlockID.WOOD, 4).shape("l").setIngredient('l', Material.LOG, -1); // Wooden Planks
        makeShaped(ItemID.STICK, 4).shape("w", "w").setIngredient('w', Material.WOOD); // Sticks
        makeShaped(BlockID.TORCH, 4).shape("c", "s").setIngredient('c', Material.COAL).setIngredient('s', Material.STICK); // Torches (coal)
        makeShaped(BlockID.TORCH, 4).shape("c", "s").setIngredient('c', Material.COAL, 1).setIngredient('s', Material.STICK); // Torches (charcoal)
        makeShaped(BlockID.WORKBENCH).shape("ww", "ww").setIngredient('w', Material.WOOD); // Workbench
        makeShaped(BlockID.FURNACE).shape("ccc", "c c", "ccc").setIngredient('c', Material.COBBLESTONE); // Furnace
        makeShaped(BlockID.CHEST).shape("www","w w","www").setIngredient('w', Material.WOOD); // Chest
        
        // Block Recipes
        makeShaped(BlockID.LAPIS_BLOCK).shape("xxx","xxx","xxx").setIngredient('x', Material.INK_SACK, 4); // Lapis
        makeShaped(ItemID.INK_SACK, 9, 4).shape("xxx","xxx","xxx").setIngredient('x', Material.LAPIS_BLOCK);
        makeShaped(BlockID.GLOWSTONE).shape("xx","xx").setIngredient('x', Material.GLOWSTONE_DUST);
        makeShaped(BlockID.WOOL).shape("xx", "xx").setIngredient('x', Material.STRING);
        makeShaped(BlockID.TNT).shape("-o-","o-o","-o-").setIngredient('-', Material.SULPHUR).setIngredient('o', Material.SAND);
        makeShaped(BlockID.COBBLESTONE_STAIRS, 4).shape("x  ","xx ","xxx").setIngredient('x', Material.COBBLESTONE);
        makeShaped(BlockID.WOOD_STAIRS, 4).shape("x  ","xx ","xxx").setIngredient('x', Material.WOOD);
        makeShaped(BlockID.SNOW_BLOCK).shape("xx","xx").setIngredient('x', Material.SNOW_BALL);
        makeShaped(BlockID.CLAY).shape("xx","xx").setIngredient('x', Material.CLAY_BALL);
        makeShaped(BlockID.BRICK).shape("xx","xx").setIngredient('x', Material.CLAY_BRICK);
        makeShaped(BlockID.BOOKSHELF).shape("www","bbb","www").setIngredient('w', Material.WOOD).setIngredient('b', Material.BOOK);
        makeShaped(BlockID.SANDSTONE).shape("xx","xx").setIngredient('x', Material.SAND);
        makeShaped(BlockID.JACK_O_LANTERN).shape("p","t").setIngredient('p', Material.PUMPKIN).setIngredient('t', Material.TORCH);
        makeShaped(BlockID.SMOOTH_BRICK, 4).shape("xx", "xx").setIngredient('x', Material.STONE);
        makeShaped(BlockID.IRON_BARS, 16).shape("xxx", "xxx").setIngredient('x', Material.IRON_INGOT);
        makeShaped(BlockID.GLASS_PANE , 16).shape("xxx", "xxx").setIngredient('x', Material.GLASS);

        // Tool Recipes
        makeShaped(ItemID.FLINT_AND_STEEL).shape("i "," f").setIngredient('i', Material.IRON_INGOT).setIngredient('f', Material.FLINT);
        makeShaped(ItemID.BUCKET).shape("i i"," i ").setIngredient('i', Material.IRON_INGOT);
        makeShaped(ItemID.COMPASS).shape(" i ","iri"," i ").setIngredient('i', Material.IRON_INGOT).setIngredient('r', Material.REDSTONE);
        makeShaped(ItemID.MAP).shape("ppp","pcp","ppp").setIngredient('p', Material.PAPER).setIngredient('c', Material.COMPASS);
        makeShaped(ItemID.WATCH).shape(" i ","iri"," i ").setIngredient('i', Material.GOLD_INGOT).setIngredient('r', Material.REDSTONE);
        makeShaped(ItemID.FISHING_ROD).shape("  /"," /s","/ s").setIngredient('/', Material.STICK).setIngredient('s', Material.STRING);
        makeShaped(ItemID.SHEARS).shape(" i","i ").setIngredient('i', Material.IRON_INGOT);
        makeShaped(ItemID.BOW).shape(" /s","/ s"," /s").setIngredient('/', Material.STICK).setIngredient('s', Material.STRING);
        makeShaped(ItemID.ARROW, 4).shape("^","/","f").setIngredient('^', Material.FLINT).setIngredient('/', Material.STICK).setIngredient('f', Material.FEATHER);
        
        // Transportation Recipes
        makeShaped(ItemID.MINECART).shape("i i","iii").setIngredient('i', Material.IRON_INGOT);
        makeShaped(ItemID.POWERED_MINECART).shape("f","m").setIngredient('f', Material.FURNACE).setIngredient('m', Material.MINECART);
        makeShaped(ItemID.STORAGE_MINECART).shape("c","m").setIngredient('c', Material.CHEST).setIngredient('m', Material.MINECART);
        makeShaped(BlockID.RAILS, 16).shape("i i","isi","i i").setIngredient('i', Material.IRON_INGOT).setIngredient('s', Material.STICK);
        makeShaped(BlockID.POWERED_RAIL, 6).shape("g g","gsg","grg").setIngredient('g', Material.GOLD_INGOT).setIngredient('s', Material.STICK).setIngredient('r', Material.REDSTONE);
        makeShaped(BlockID.DETECTOR_RAIL, 6).shape("i i","ipi","iri").setIngredient('i', Material.IRON_INGOT).setIngredient('p', Material.STONE_PLATE).setIngredient('r', Material.REDSTONE);
        makeShaped(ItemID.BOAT).shape("w w","www").setIngredient('w', Material.WOOD);
        
        // Mechanism Recipes

        makeShaped(ItemID.WOOD_DOOR).shape("ww","ww","ww").setIngredient('w', Material.WOOD);
        makeShaped(ItemID.IRON_DOOR).shape("ii","ii","ii").setIngredient('i', Material.IRON_INGOT);
        makeShaped(BlockID.TRAP_DOOR, 2).shape("www","www").setIngredient('w', Material.WOOD);
        makeShaped(BlockID.WOOD_PLATE).shape("ww").setIngredient('w', Material.WOOD);
        makeShaped(BlockID.STONE_PLATE).shape("ss").setIngredient('s', Material.STONE);
        makeShaped(BlockID.STONE_BUTTON).shape("s","s").setIngredient('s', Material.STONE);
        makeShaped(BlockID.REDSTONE_TORCH_ON).shape("r","/").setIngredient('r', Material.REDSTONE).setIngredient('/', Material.STICK);
        makeShaped(BlockID.LEVER).shape("/","s").setIngredient('s', Material.COBBLESTONE).setIngredient('/', Material.STICK);
        makeShaped(BlockID.NOTE_BLOCK).shape("www","wrw","www").setIngredient('w', Material.WOOD).setIngredient('r', Material.REDSTONE);
        makeShaped(BlockID.JUKEBOX).shape("www","wdw","www").setIngredient('w', Material.WOOD).setIngredient('d', Material.DIAMOND);
        makeShaped(BlockID.DISPENSER).shape("ccc", "cbc", "crc").setIngredient('c', Material.COBBLESTONE).setIngredient('b', Material.BOW).setIngredient('r', Material.REDSTONE);
        makeShaped(ItemID.DIODE).shape("trt","sss").setIngredient('t', Material.REDSTONE_TORCH_ON).setIngredient('r', Material.REDSTONE).setIngredient('s', Material.STONE);
        makeShaped(BlockID.PISTON_BASE).shape("www","sis","srs").setIngredient('w', Material.WOOD).setIngredient('s', Material.COBBLESTONE).setIngredient('i', Material.IRON_INGOT).setIngredient('r', Material.REDSTONE);
        makeShaped(BlockID.PISTON_STICKY_BASE).shape("s","p").setIngredient('s', Material.SLIME_BALL).setIngredient('p', Material.PISTON_BASE);
        makeShaped(BlockID.FENCE_GATE).shape("#W#", "#W#").setIngredient('#', Material.STICK).setIngredient('W', Material.WOOD);
        
        // Food Recipes

        makeShaped(ItemID.BOWL, 4).shape("w w", " w ").setIngredient('w', Material.WOOD);
        makeShaped(ItemID.MUSHROOM_SOUP).shape("r","b","u").setIngredient('r', Material.RED_MUSHROOM).setIngredient('b', Material.BROWN_MUSHROOM).setIngredient('u', Material.BOWL);
        makeShaped(ItemID.MUSHROOM_SOUP).shape("b","r","u").setIngredient('r', Material.RED_MUSHROOM).setIngredient('b', Material.BROWN_MUSHROOM).setIngredient('u', Material.BOWL);
        makeShaped(ItemID.BREAD).shape("www").setIngredient('w', Material.WHEAT);
        makeShaped(ItemID.SUGAR).shape("s").setIngredient('s', Material.SUGAR_CANE);
        makeShaped(ItemID.CAKE).shape("mmm","ses","www").setIngredient('m', Material.MILK_BUCKET).setIngredient('s', Material.SUGAR).setIngredient('e', Material.EGG).setIngredient('w', Material.WHEAT);
        makeShaped(ItemID.COOKIE, 8).shape("wdw").setIngredient('w', Material.WHEAT).setIngredient('d', Material.INK_SACK, 3);
        makeShaped(ItemID.GOLDEN_APPLE).shape("ggg","gag","ggg").setIngredient('g', Material.GOLD_BLOCK).setIngredient('a', Material.APPLE);
        makeShaped(BlockID.MELON_BLOCK).shape("mmm", "mmm", "mmm").setIngredient('m', Material.MELON);
        makeShaped(ItemID.MELON_SEEDS).shape("m").setIngredient('m', Material.MELON);

        // Food Recipes

        
        // Miscellaneous Recipes
        makeShaped(ItemID.PAINTING).shape("///","/w/","///").setIngredient('/', Material.STICK).setIngredient('w', Material.WOOL);
        makeShaped(ItemID.SIGN).shape("www", "www", " / ").setIngredient('w', Material.WOOD).setIngredient('/', Material.STICK);
        makeShaped(BlockID.LADDER, 2).shape("/ /","///","/ /").setIngredient('/', Material.STICK);
        makeShaped(ItemID.PAPER, 3).shape("sss").setIngredient('s', Material.SUGAR_CANE);
        makeShaped(ItemID.BOOK).shape("p","p","p").setIngredient('p', Material.PAPER);
        makeShaped(BlockID.FENCE, 2).shape("///","///").setIngredient('/', Material.STICK);
        makeShaped(ItemID.BED).shape("ccc","www").setIngredient('c', Material.WOOL).setIngredient('w', Material.WOOD);

        // Dye Recipes
        makeShapeless(ItemID.INK_SACK, 2, 1).addIngredient(Material.RED_ROSE); // Rose Red
        // 2 = cactus [smelt], 3 = cocoa [dungeon], 4 = lapis [mine]
        makeShapeless(ItemID.INK_SACK, 2, 5).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 1); // Purple
        makeShapeless(ItemID.INK_SACK, 2, 6).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 2); // Cyan
        makeShapeless(ItemID.INK_SACK, 3, 7).addIngredient(Material.INK_SACK).addIngredient(2, Material.INK_SACK, 15); // Light gray
        makeShapeless(ItemID.INK_SACK, 2, 7).addIngredient(Material.INK_SACK, 8).addIngredient(Material.INK_SACK, 15); // Light gray
        makeShapeless(ItemID.INK_SACK, 2, 8).addIngredient(Material.INK_SACK).addIngredient(Material.INK_SACK, 15); // Gray
        makeShapeless(ItemID.INK_SACK, 2, 9).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 15); // Pink
        makeShapeless(ItemID.INK_SACK, 2, 10).addIngredient(Material.INK_SACK, 2).addIngredient(Material.INK_SACK, 15); // Lime
        makeShapeless(ItemID.INK_SACK, 2, 11).addIngredient(Material.YELLOW_FLOWER); // Dandelion Yellow
        makeShapeless(ItemID.INK_SACK, 2, 12).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15); // Light blue
        makeShapeless(ItemID.INK_SACK, 2, 13).addIngredient(Material.INK_SACK, 5).addIngredient(Material.INK_SACK, 9); // Magenta
        makeShapeless(ItemID.INK_SACK, 3, 13).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 9).addIngredient(Material.INK_SACK, 1); // Magenta
        makeShapeless(ItemID.INK_SACK, 4, 13).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15).addIngredient(2, Material.INK_SACK, 1); // Magenta
        makeShapeless(ItemID.INK_SACK, 2, 14).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 11); // Orange
        makeShapeless(ItemID.INK_SACK, 3, 15).addIngredient(Material.BONE); // Bonemeal
        
        // Wool Recipies
        for (int i = 0; i < 16; ++i) {
            makeShapeless(BlockID.WOOL, 1, i).addIngredient(Material.WOOL).addIngredient(Material.INK_SACK, 15 - i);
        }
        
        // Smelting Recipes

        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.IRON_INGOT), Material.IRON_ORE));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.GOLD_INGOT), Material.GOLD_ORE));
        addRecipe(new FurnaceRecipe(new ItemStack(BlockID.GLASS), Material.SAND));
        addRecipe(new FurnaceRecipe(new ItemStack(BlockID.STONE), Material.COBBLESTONE));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.GRILLED_PORK), Material.PORK));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.COOKED_BEEF), Material.RAW_BEEF));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.COOKED_CHICKEN), Material.RAW_CHICKEN));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.CLAY_BRICK), Material.CLAY));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.COOKED_FISH), Material.RAW_FISH));
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.COAL, 1, (byte)1), Material.LOG)); // Charcoal
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.INK_SACK, 1, (byte)2), Material.CACTUS)); // Cactus green
        addRecipe(new FurnaceRecipe(new ItemStack(ItemID.DIAMOND), Material.DIAMOND_ORE));
        
        // Smelting fuels (time is in ticks)
        furnaceFuels.put(ItemID.COAL, 1600);
        furnaceFuels.put(BlockID.WOOD, 300);
        furnaceFuels.put(BlockID.SAPLING, 100);
        furnaceFuels.put(ItemID.STICK, 100);
        furnaceFuels.put(BlockID.FENCE, 300);
        furnaceFuels.put(BlockID.WOOD_STAIRS, 400);
        furnaceFuels.put(BlockID.TRAP_DOOR, 300);
        furnaceFuels.put(BlockID.LOG, 300);
        furnaceFuels.put(BlockID.WORKBENCH, 300);
        furnaceFuels.put(BlockID.BOOKSHELF, 300);
        furnaceFuels.put(BlockID.CHEST, 300);
        furnaceFuels.put(BlockID.JUKEBOX, 300);
        furnaceFuels.put(BlockID.NOTE_BLOCK, 300);
        furnaceFuels.put(BlockID.LOCKED_CHEST, 300);
        furnaceFuels.put(ItemID.LAVA_BUCKET, 20000);
    }
    
    // -- Helper functions
    
    private ShapedRecipe makeShaped(int mat) {
        return makeShaped(mat, 1, 0);
    }
    
    private ShapedRecipe makeShaped(int mat, int amount) {
        return makeShaped(mat, amount, 0);
    }
    
    private ShapedRecipe makeShaped(int mat, int amount, int data) {
        ShapedRecipe result = new ShapedRecipe(new ItemStack(mat, amount, (byte) data));
        addRecipe(result);
        return result;
    }
    
    private ShapelessRecipe makeShapeless(int mat, int amount, int data) {
        ShapelessRecipe result = new ShapelessRecipe(new ItemStack(mat, amount, (byte) data));
        addRecipe(result);
        return result;
    }
    
    private enum CraftingSet {        
        WOOD(Material.WOOD,
                tools(ItemID.WOOD_AXE, ItemID.WOOD_PICKAXE, ItemID.WOOD_SPADE, ItemID.WOOD_HOE, ItemID.WOOD_SWORD), stairs(2, BlockID.WOOD_STAIRS)),
        COBBLESTONE(Material.COBBLESTONE,
                tools(ItemID.STONE_AXE, ItemID.STONE_PICKAXE, ItemID.STONE_SPADE, ItemID.STONE_HOE, ItemID.STONE_SWORD), stairs(3, BlockID.COBBLESTONE_STAIRS)),
        GOLD(Material.GOLD_INGOT, block(Material.GOLD_BLOCK),
                tools(ItemID.GOLD_AXE, ItemID.GOLD_PICKAXE, ItemID.GOLD_SPADE, ItemID.GOLD_HOE, ItemID.GOLD_SWORD),
                armor(ItemID.GOLD_HELMET, ItemID.GOLD_CHESTPLATE, ItemID.GOLD_LEGGINGS, ItemID.GOLD_BOOTS)),
        IRON(Material.IRON_INGOT, block(Material.IRON_BLOCK),
                tools(ItemID.IRON_AXE, ItemID.IRON_PICKAXE, ItemID.IRON_SPADE, ItemID.IRON_HOE, ItemID.IRON_SWORD),
                armor(ItemID.IRON_HELMET, ItemID.IRON_CHESTPLATE, ItemID.IRON_LEGGINGS, ItemID.IRON_BOOTS)),
        DIAMOND(Material.DIAMOND, block(Material.DIAMOND_BLOCK),
                tools(ItemID.DIAMOND_AXE, ItemID.DIAMOND_PICKAXE, ItemID.DIAMOND_SPADE, ItemID.DIAMOND_HOE, ItemID.DIAMOND_SWORD),
                armor(ItemID.DIAMOND_HELMET, ItemID.DIAMOND_CHESTPLATE, ItemID.DIAMOND_LEGGINGS, ItemID.DIAMOND_BOOTS)),
        LEATHER(Material.LEATHER,
                armor(ItemID.LEATHER_HELMET, ItemID.LEATHER_CHESTPLATE, ItemID.LEATHER_LEGGINGS, ItemID.LEATHER_BOOTS)),
        CHAINMAIL(Material.FIRE,
               armor(ItemID.CHAINMAIL_HELMET, ItemID.CHAINMAIL_CHESTPLATE, ItemID.CHAINMAIL_LEGGINGS, ItemID.CHAINMAIL_BOOTS)),
        BRICK(Material.BRICK, stairs(4, BlockID.BRICK_STAIRS)),
        SMOOTH_BRICK(Material.SMOOTH_BRICK, stairs(5, BlockID.SMOOTH_STAIRS)),
        SANDSTONE(Material.SANDSTONE, stairs(1, -1)),
        STONE(Material.STONE, stairs(0, -1));
        
        
        private Material material;
        private Material block;

        private int[] armor;
        private int[] tools;
        private int stairs;
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
        
        public int[] getArmor() {
            return armor;
        }
        
        public int[] getTools() {
            return tools;
        }

        public int getStairMaterial() {
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
        
        private static Property tools(final int axe, final int pick, final int shovel, final int hoe, final int sword) {
            return new Property() { public void apply(CraftingSet s) {
                s.tools = new int[] { axe, pick, shovel, hoe, sword };
            }};
        }
        
        private static Property armor(final int helmet, final int chestplate, final int leggings, final int boots) {
            return new Property() { public void apply(CraftingSet s) {
                s.armor = new int[] { helmet, chestplate, leggings, boots };
            }};
        }

        private static Property stairs(final int slabData, final int stairs) {
            return new Property() { public void apply(CraftingSet set) {
                set.stairs = stairs;
                set.slabData = slabData;
            }};
        }
        
    }
    
}
