package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowParrot;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public class ParrotStore extends TameableStore<GlowParrot> {

    public ParrotStore() {
        super(GlowParrot.class, EntityType.PARROT, GlowParrot::new);
    }

    @Override
    public void load(GlowParrot entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.containsKey("Variant")) {
            entity.setVariant(GlowParrot.VARIANTS[compound.getInt("Variant")]);
        }
    }

    @Override
    public void save(GlowParrot entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Variant", entity.getVariant().ordinal());
    }
}
