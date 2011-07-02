package net.glowstone.inventory;

import java.util.ArrayList;
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
    
    private ArrayList<ShapedRecipe> shapedRecipes = new ArrayList<ShapedRecipe>();
    private ArrayList<ShapelessRecipe> shapelessRecipes = new ArrayList<ShapelessRecipe>();
    private ArrayList<FurnaceRecipe> furnaceRecipes = new ArrayList<FurnaceRecipe>();
    
    public CraftingManager() {
        resetRecipes();
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
    public FurnaceRecipe getFurnace(ItemStack input) {
        for (FurnaceRecipe recipe : furnaceRecipes) {
            if (recipe.getInput().equals(input)) {
                return recipe;
            }
        }
        
        return null;
    }
    
    public Recipe get2by2(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Expected ItemStack[4], got ItemStack[" + items.length + "]");
        }
        
        for (ShapedRecipe recipe : shapedRecipes) {
            
        }
        
        return getShapeless(items);
    }
    
    public Recipe get3by3(ItemStack[] items) {
        if (items.length != 9) {
            throw new IllegalArgumentException("Expected ItemStack[9], got ItemStack[" + items.length + "]");
        }
        
        for (ShapedRecipe recipe : shapedRecipes) {
            
        }
        
        return getShapeless(items);
    }
    
    private ShapelessRecipe getShapeless(ItemStack[] items) {
        for (ShapelessRecipe recipe : shapelessRecipes) {
            boolean failed = false;
            boolean[] accountedFor = new boolean[items.length];
            
            // Make sure each ingredient in the recipe exists in the inventory
            for (MaterialData ingredient : recipe.getIngredientList()) {
                boolean found = false;
                for (int i = 0; i < items.length; ++i) {
                    if (!accountedFor[i] && items[i].getData().equals(ingredient)) {
                        found = true;
                        accountedFor[i] = true;
                        break;
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
    
    public void resetRecipes() {
        shapedRecipes.clear();
        shapelessRecipes.clear();
        furnaceRecipes.clear();
        
        // Crafting sets
        for (CraftingSet set : CraftingSet.values()) {
            if (set.getBlockSize() == 2) {
                addRecipe(new ShapedRecipe(new ItemStack(set.getBlock(), 1)).shape("xx", "xx").setIngredient('x', set.getInput()));
            } else if (set.getBlockSize() == 3) {
                addRecipe(new ShapedRecipe(new ItemStack(set.getBlock(), 1)).shape("xxx", "xxx", "xxx").setIngredient('x', set.getInput()));
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
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD, 4)).shape("l").setIngredient('l', Material.LOG)); // Wooden Planks
        addRecipe(new ShapedRecipe(new ItemStack(Material.STICK, 4)).shape("w", "w").setIngredient('w', Material.WOOD)); // Sticks
        addRecipe(new ShapedRecipe(new ItemStack(Material.TORCH, 4)).shape("c", "s").setIngredient('c', Material.COAL).setIngredient('s', Material.STICK)); // Torches
        addRecipe(new ShapedRecipe(new ItemStack(Material.WORKBENCH)).shape("ww", "ww").setIngredient('w', Material.WOOD)); // Workbench
        addRecipe(new ShapedRecipe(new ItemStack(Material.FURNACE)).shape("ccc", "c c", "ccc").setIngredient('c', Material.COBBLESTONE)); // Furnace
        addRecipe(new ShapedRecipe(new ItemStack(Material.CHEST)).shape("www","w w","www").setIngredient('w', Material.WOOD)); // Chest
        
        // Block Recipes
        addRecipe(new ShapedRecipe(new ItemStack(Material.LAPIS_BLOCK)).shape("xxx","xxx","xxx").setIngredient('x', Material.INK_SACK, 4)); // Lapis
        addRecipe(new ShapedRecipe(new ItemStack(Material.TNT)).shape("-o-","o-o","-o-").setIngredient('-', Material.SULPHUR).setIngredient('o', Material.SAND)); // TNT
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 1, (byte)0)).shape("xxx").setIngredient('x', Material.STONE)); // Slab #0
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 1, (byte)1)).shape("xxx").setIngredient('x', Material.SANDSTONE)); // Slab #1
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 1, (byte)2)).shape("xxx").setIngredient('x', Material.WOOD)); // Slab #2
        addRecipe(new ShapedRecipe(new ItemStack(Material.STEP, 1, (byte)3)).shape("xxx").setIngredient('x', Material.COBBLESTONE)); // Slab #3
        addRecipe(new ShapedRecipe(new ItemStack(Material.COBBLESTONE_STAIRS, 1)).shape("x  ","xx ","xxx").setIngredient('x', Material.COBBLESTONE)); // Stairs one
        addRecipe(new ShapedRecipe(new ItemStack(Material.WOOD)).shape("x  ","xx ","xxx").setIngredient('x', Material.WOOD)); // Stairs two
        addRecipe(new ShapedRecipe(new ItemStack(Material.BOOKSHELF)).shape("www","bbb","www").setIngredient('w', Material.WOOD).setIngredient('b', Material.BOOK)); // Bookshelf
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
        addRecipe(new ShapedRecipe(new ItemStack(Material.ARROW)).shape("^","|","f").setIngredient('^', Material.FLINT).setIngredient('|', Material.STICK).setIngredient('f', Material.FEATHER)); // Arrow
        
        // Transportation Recipes
        
        // Mechanism Recipes
        
        // Food Recipes
        
        // Miscellaneous Recipes
        
        // Dye Recipes
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 1)).addIngredient(Material.RED_ROSE)); // Rose Red
        // 2 = cactus [smelt], 3 = cocoa [dungeon], 4 = lapis [mine]
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 5)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 1)); // Purple
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 6)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 2)); // Cyan
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 7)).addIngredient(Material.INK_SACK).addIngredient(2, Material.INK_SACK, 15)); // Light gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 7)).addIngredient(Material.INK_SACK, 8).addIngredient(Material.INK_SACK, 15)); // Light gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 8)).addIngredient(Material.INK_SACK).addIngredient(Material.INK_SACK, 15)); // Gray
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 9)).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 15)); // Pink
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 10)).addIngredient(Material.INK_SACK, 2).addIngredient(Material.INK_SACK, 15)); // Lime
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 11)).addIngredient(Material.YELLOW_FLOWER)); // Dandelion Yellow
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 12)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15)); // Light blue
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 13)).addIngredient(Material.INK_SACK, 5).addIngredient(Material.INK_SACK, 9)); // Magenta
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 13)).addIngredient(Material.INK_SACK, 4).addIngredient(Material.INK_SACK, 15).addIngredient(2, Material.INK_SACK, 1)); // Magenta
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 14)).addIngredient(Material.INK_SACK, 1).addIngredient(Material.INK_SACK, 11)); // Orange
        addRecipe(new ShapelessRecipe(new ItemStack(Material.INK_SACK, 1, (byte) 15)).addIngredient(Material.BONE)); // Bonemeal
        
        
        // Wool Recipies
        for (int i = 0; i < 16; ++i) {
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
        
        // Yay!
        GlowServer.logger.log(Level.INFO, "Registered {0},{1},{2} recipes", new Object[]{shapedRecipes.size(), shapelessRecipes.size(), furnaceRecipes.size()});
    }
    
    private enum CraftingSet {
        SNOW(Material.SNOW_BALL, block(2, Material.SNOW_BLOCK)),
        CLAY(Material.CLAY_BALL, block(2, Material.CLAY)),
        BRICK(Material.CLAY_BRICK, block(2, Material.BRICK)),
        SANDSTONE(Material.SAND, block(2, Material.SANDSTONE)),
        GLOWSTONE(Material.GLOWSTONE_DUST, block(2, Material.GLOWSTONE)),
        WOOL(Material.STRING, block(2, Material.WOOL)),
        
        WOOD(Material.WOOD,
                tools(Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.WOOD_SPADE, Material.WOOD_HOE, Material.WOOD_SWORD)),
        STONE(Material.COBBLESTONE,
                tools(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_SPADE, Material.STONE_HOE, Material.STONE_SWORD)),
        GOLD(Material.GOLD_INGOT, block(3, Material.GOLD_BLOCK),
                tools(Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.GOLD_SPADE, Material.GOLD_HOE, Material.GOLD_SWORD),
                armor(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS)),
        IRON(Material.IRON_INGOT, block(3, Material.IRON_BLOCK),
                tools(Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SPADE, Material.IRON_HOE, Material.IRON_SWORD),
                armor(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS)),
        DIAMOND(Material.DIAMOND, block(3, Material.DIAMOND_BLOCK),
                tools(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SPADE, Material.DIAMOND_HOE, Material.DIAMOND_SWORD),
                armor(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS)),
        LEATHER(Material.LEATHER,
                armor(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS)),
        CHAINMAIL(Material.FIRE,
                armor(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS));
        
        
        private Material material;
        private Material block;
        private int blockSize;
        private boolean unblock;
        
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
        
        public int getBlockSize() {
            return blockSize;
        }
        
        public Material[] getArmor() {
            return armor;
        }
        
        public Material[] getTools() {
            return tools;
        }
        
        public boolean getUnblock() {
            return unblock;
        }
        
        // -----------------
    
        private interface Property {
            void apply(CraftingSet set);
        }
        
        private static Property block(final int size, final Material mat) {
            return new Property() { public void apply(CraftingSet s) {
                s.blockSize = size;
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
