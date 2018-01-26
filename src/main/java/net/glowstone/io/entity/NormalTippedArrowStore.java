package net.glowstone.io.entity;

import net.glowstone.entity.projectile.GlowArrow;
import net.glowstone.entity.projectile.GlowTippedArrow;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

public class NormalTippedArrowStore extends ArrowStore<GlowArrow> {
    public NormalTippedArrowStore() {
        super(GlowArrow.class, "arrow");
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
