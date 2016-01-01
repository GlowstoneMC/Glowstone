package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowWolf;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.DyeColor;

class WolfStore extends TameableStore<GlowWolf> {

    public WolfStore() {
        super(GlowWolf.class, "Ozelot");
    }

    @Override
    public void load(GlowWolf entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setCollarColor(DyeColor.getByData(compound.getByte("CollarColor")));
        entity.setAngry(compound.getBool("Angry"));
    }

    @Override
    public void save(GlowWolf entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("CollarColor", entity.getCollarColor().getData());
        tag.putBool("Angry", entity.isAngry());
    }

}
