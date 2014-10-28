package net.glowstone.inventory;

import org.bukkit.BannerPattern;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.DynamicRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Dye;

import java.util.List;

/**
 * Recipe for adding patterns to a banner item.
 */
public class GlowBannerRecipe implements DynamicRecipe {

    ItemStack result = new ItemStack(Material.BANNER); // Default result

    @Override
    public boolean match(ItemStack[] matrix) {
        DyeColor color = null;
        BannerPattern.LayerTexture texture = null;
        ItemStack banner = null;

        for(ItemStack item : matrix) {
            if(item == null) continue;
            if(item.getType() == Material.BANNER) {
                if(banner != null) {
                    return false; // Multiple banners found
                }
                banner = item;
                continue;
            }
            if(item.getType() == Material.INK_SACK) {
                DyeColor itemColor = ((Dye) item.getData()).getColor();
                if(color != null && itemColor != color) {
                    return false; // Can't have multiple colors
                }
                color = itemColor;
            }
        }
        if(banner == null) {
            return false; // Couldn't found a banner to alter
        }

        recipe:
        for(LayerRecipe recipe : LayerRecipe.values()) {
            if(recipe.hasItem()) {
                boolean foundDye = false;
                for(ItemStack item : matrix) {
                    if(item == null) continue; // Ignore blanks

                    if(item.getType() == Material.BANNER) continue; // Banner is already checked

                    if(item.getType() == Material.INK_SACK) {
                        if(foundDye) continue recipe; // Can't have multiple dyes
                        foundDye = true;
                        continue;
                    }

                    if(item.getType() == recipe.getType() && item.getDurability() == recipe.getData()) {
                        if(texture != null) return false; // Can't have multiple of same item
                        texture = recipe.getPattern(); // Matches texture type
                        continue;
                    }
                    continue recipe; // Non-recipe item in grid
                }
                if(texture == null) {
                    continue; // No item type for this recipe found
                }
                if(color == null) {
                    color = DyeColor.BLACK;
                }
                break; // Recipe matches
            } else {
                if(matrix.length != 9) return false; // Non-item recipes only work on 3x3

                for(int i = 0; i < 9; i++) {
                    boolean hasValue = recipe.getValues()[i] == '#';
                    ItemStack item = matrix[i];
                    if(hasValue && item != null && item.getType() == Material.INK_SACK) {
                        continue;
                    }
                    if(!hasValue && (item == null || item.getType() == Material.BANNER)) {
                        continue; // Allow banner and blanks
                    }
                    continue recipe; // Non-recipe item found or no dye where dye should be
                }
                texture = recipe.getPattern();
                break; // Recipe matches
            }
        }

        if(texture == null) return false; // No texture found

        // Create result banner
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        List<BannerPattern.BannerLayer> layers = meta.getPattern().getLayers();
        BannerPattern.Builder builder = BannerPattern.builder();
        for(BannerPattern.BannerLayer layer : layers) {
            builder.layer(layer.getTexture(), layer.getColor());
        }
        builder.layer(texture, color);
        meta.setPattern(builder.build());
        result = banner.clone();
        result.setItemMeta(meta);
        return true;
    }

    @Override
    public ItemStack getResult() {
        return result;
    }

    private enum LayerRecipe {
        BORDER("###", "# #", "###"),
        BOTTOM_HALF("   ", "###", "###"),
        BRICK(Material.BRICK),
        CIRCLE("   ", " # ", "   "),
        CREEPER(Material.SKULL_ITEM, (short) 4),
        CURLY_BORDER(Material.VINE),
        DIAGONAL_CROSS("# #", " # ", "# #"),
        FLOWER(Material.RED_ROSE, (short) 8),
        GRADIENT("# #", " # ", " # "),
        GRADIENT_UP(" # ", " # ", "# #"),
        LEFT_HALF("## ", "## ", "## "),
        MOJANG(Material.GOLDEN_APPLE),
        RIGHT_HALF(" ##", " ##", " ##"),
        RHOMBUS(" # ", "# #", " # "),
        SAW_BOTTOM("   ", "# #", " # "),
        SAW_TOP(" # ", "# #", "   "),
        SKULL(Material.SKULL_ITEM, (short) 1),
        STRIPES("# #", "# #", "   "),
        SQUARE_BOTTOM_LEFT("   ", "   ", "#  "),
        SQUARE_BOTTOM_RIGHT("   ", "   ", "  #"),
        SQUARE_TOP_LEFT("#  ", "   ", "   "),
        SQUARE_TOP_RIGHT("  #", "   ", "   "),
        STRIPE_BOTTOM("   ", "   ", "###"),
        STRAIGHT_CROSS(" # ", "###", " # "),
        STRIPE_DOWN_LEFT("  #", " # ", "#  "),
        STRIPE_DOWN_RIGHT("#  ", " # ", "  #"),
        STRIPE_LEFT("#  ", "#  ", "#  "),
        STRIPE_HORIZONTAL("   ", "###", "   "),
        STRIPE_RIGHT("  #", "  #", "  #"),
        STRIPE_TOP("###", "   ", "   "),
        STRIPE_VERTICAL(" # ", " # ", " # "),
        TOP_HALF("###", "###", "   "),
        TRIANGLE_BOTTOM("   ", " # ", "# #"),
        TRIANGLE_BOTTOM_LEFT("   ", "#  ", "## "),
        TRIANGLE_BOTTOM_RIGHT("   ", "  #", " ##"),
        TRIANGLE_TOP("# #", " # ", "   "),
        TRIANGLE_TOP_LEFT("## ", "#  ", "   "),
        TRIANGLE_TOP_RIGHT(" ##", "  #", "   ");

        char[] values;
        Material item;
        short data;

        private LayerRecipe(String... rows) {
            values = new char[9];
            int index = 0;
            for(String row : rows) {
                for(char c : row.toCharArray()) {
                    values[index++] = c;
                }
            }
        }

        private LayerRecipe(Material item) {
            this.item = item;
        }

        private LayerRecipe(Material item, short data) {
            this.item = item;
            this.data = data;
        }

        public char[] getValues() {
            return values;
        }

        public boolean hasItem() {
            return item != null;
        }

        public Material getType() {
            return item;
        }

        public short getData() {
            return data;
        }

        public BannerPattern.LayerTexture getPattern() {
            return BannerPattern.LayerTexture.valueOf(name());
        }
    }
}
