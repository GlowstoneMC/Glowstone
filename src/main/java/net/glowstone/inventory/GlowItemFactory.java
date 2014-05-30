package net.glowstone.inventory;

import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * An implementation of {@link ItemFactory} responsible for creating ItemMetas.
 */
public class GlowItemFactory implements ItemFactory {

    private static final GlowItemFactory instance = new GlowItemFactory();
    private static final Color LEATHER_COLOR = Color.fromRGB(0xA06540);

    private GlowItemFactory() {
    }

    public ItemMeta getItemMeta(Material material) {
        return makeMeta(material, null);
    }

    public boolean isApplicable(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        return isApplicable(meta, stack.getType());
    }

    public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
        return meta != null && material != null && toGlowMeta(meta).isApplicable(material);
    }

    public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {
        // in the future, do fancy comparisons
        return meta1 == meta2;
    }

    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        return asMetaFor(meta, stack.getType());
    }

    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        return makeMeta(material, toGlowMeta(meta));
    }

    public Color getDefaultLeatherColor() {
        return LEATHER_COLOR;
    }

    public CompoundTag writeNbt(ItemMeta meta) {
        CompoundTag result = new CompoundTag();
        toGlowMeta(meta).writeNbt(result);
        return result.isEmpty() ? null : result;
    }

    public ItemMeta readNbt(Material material, CompoundTag tag) {
        if (tag == null) return null;
        GlowMetaItem meta = makeMeta(material, null);
        if (meta == null) return null;
        meta.readNbt(tag);
        return meta;
    }

    /**
     * Get the static GlowItemFactory instance.
     * @return The instance.
     */
    public static GlowItemFactory instance() {
        return instance;
    }

    /**
     * Throw a descriptive error if the given ItemMeta does not belong to this factory.
     * @param meta The ItemMeta.
     * @return The GlowMetaItem.
     */
    private GlowMetaItem toGlowMeta(ItemMeta meta) {
        if (meta instanceof GlowMetaItem) {
            return (GlowMetaItem) meta;
        }
        throw new IllegalArgumentException("Item meta " + meta + " was not created by GlowItemFactory");
    }

    /**
     * Get a suitable ItemMeta for the material, reusing the provided meta if non-null and possible.
     */
    private GlowMetaItem makeMeta(Material material, GlowMetaItem meta) {
        // in the future, more specific metas
        switch (material) {
            case AIR:
                return null;
            default:
                return new GlowMetaItem(meta);
        }
    }
}
