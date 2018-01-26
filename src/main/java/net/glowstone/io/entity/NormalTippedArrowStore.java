package net.glowstone.io.entity;

import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TippedArrow;

public class NormalTippedArrowStore extends ArrowStore<GlowArrow> {
    public NormalTippedArrowStore() {
        super(GlowArrow.class, "arrow");
    }

    @Override
    public void save(GlowArrow entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity instanceof GlowTippedArrow) {
            final GlowTippedArrow tippedArrow = (GlowTippedArrow) entity;
            final int colorRgb = tippedArrow.getColor().asRGB();
            tag.putInt("Color", colorRgb);
            tag.putInt("CustomPotionColor", colorRgb);
            tag.putList("CustomPotionEffects", TagType.COMPOUND, tippedArrow.getCustomEffects());
            tag.putString("Potion", tippedArrow.getBasePotionData().toString());
        }
    }

    @Override
    public void load(GlowArrow entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof TippedArrow) {
            if (tag.isInt("Color")) {
                ((TippedArrow) entity).setColor(Color.fromRGB(tag.getInt("Color")));
            }
            if (tag.isList("CustomPotionEffects", TagType.COMPOUND)) {

            }
        }
    }

    @Override
    public GlowArrow createEntity(Location location, CompoundTag compound) {
        if (compound.isCompound("Potion") || compound.isCompound("CustomPotionEffects")) {
            // arrows with these fields are tipped
            return new GlowTippedArrow(location);
        }
        return super.createEntity(location, compound);
    }
}
