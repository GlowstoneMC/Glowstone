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
public final class GlowItemFactory implements ItemFactory {

    private static final GlowItemFactory instance = new GlowItemFactory();
    private static final Color LEATHER_COLOR = Color.fromRGB(0xA06540);

    private GlowItemFactory() {
    }

    @Override
    public ItemMeta getItemMeta(Material material) {
        return makeMeta(material, null);
    }

    @Override
    public boolean isApplicable(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        return isApplicable(meta, stack.getType());
    }

    @Override
    public boolean isApplicable(ItemMeta meta, Material material) throws IllegalArgumentException {
        return meta != null && material != null && toGlowMeta(meta).isApplicable(material);
    }

    @Override
    public boolean equals(ItemMeta meta1, ItemMeta meta2) throws IllegalArgumentException {
        // todo: be nicer about comparisons without involving serialization
        // and the extra new objects for null arguments
        GlowMetaItem glow1, glow2;
        if (meta1 == null) {
            glow1 = new GlowMetaItem(null);
        } else {
            glow1 = toGlowMeta(meta1);
        }
        if (meta2 == null) {
            glow2 = new GlowMetaItem(null);
        } else {
            glow2 = toGlowMeta(meta2);
        }
        return glow1.serialize().equals(glow2.serialize());
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, ItemStack stack) throws IllegalArgumentException {
        return makeMeta(stack.getType(), toGlowMeta(meta));
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta meta, Material material) throws IllegalArgumentException {
        return makeMeta(material, toGlowMeta(meta));
    }

    @Override
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
        // todo: more specific metas
        switch (material) {
            case AIR:
                return null;
            case BOOK_AND_QUILL:
            case WRITTEN_BOOK:
                return new GlowMetaBook(meta);
            default:
                return new GlowMetaItem(meta);
        }
    }
}
