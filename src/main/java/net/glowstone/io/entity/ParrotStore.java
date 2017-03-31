package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowParrot;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public class ParrotStore extends TameableStore<GlowParrot> {

    public ParrotStore() {
        super(GlowParrot.class, EntityType.PARROT);
    }

    @Override
    public void load(GlowParrot entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setVariant(GlowParrot.VARIANTS[compound.getInt("Variant")]);
        entity.setSitting(compound.getBool("Sitting"));
    }

    @Override
    public void save(GlowParrot entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Variant", entity.getVariant().ordinal());
        tag.putBool("Sitting", entity.isSitting());
    }
}
