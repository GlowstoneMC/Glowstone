package net.glowstone.inventory;

import net.glowstone.util.nbt.CompoundTag;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Map;

public class GlowMetaLeatherArmor extends GlowMetaItem implements LeatherArmorMeta {

    private Color color = GlowItemFactory.instance().getDefaultLeatherColor();

    public GlowMetaLeatherArmor(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaLeatherArmor)) {
            return;
        }
        GlowMetaLeatherArmor armor = (GlowMetaLeatherArmor) meta;
        color = armor.color;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        Validate.notNull(color, "Armor color cannot be null");
        this.color = color;
    }

    @Override
    public boolean isApplicable(Material material) {
        switch (material) {
            case LEATHER_HELMET:
            case LEATHER_CHESTPLATE:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "LEATHER_ARMOR");
        if (hasColor()) {
            result.put("color", color.serialize());
        }
        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);

        if (hasColor()) {
            CompoundTag display = new CompoundTag();
            display.putInt("color", color.asRGB());
            tag.putCompound("display", display);
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);

        if (tag.isCompound("display")) {
            CompoundTag display = tag.getCompound("display");
            if (display.isInt("color")) {
                this.color = Color.fromRGB(display.getInt("color"));
            }
        }
    }

    @Override
    public LeatherArmorMeta clone() {
        return new GlowMetaLeatherArmor(this);
    }

    private boolean hasColor() {
        return !color.equals(GlowItemFactory.instance().getDefaultLeatherColor());
    }
}
