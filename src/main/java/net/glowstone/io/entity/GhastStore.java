package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowGhast;
import net.glowstone.util.nbt.CompoundTag;

class GhastStore extends MonsterStore<GlowGhast> {

    public GhastStore() {
        super(GlowGhast.class, "Ghast");
    }

    @Override
    public void load(GlowGhast entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setExplosionPower(compound.getInt("ExplosionPower"));
    }

    @Override
    public void save(GlowGhast entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putInt("ExplosionPower", entity.getExplosionPower());
    }

}
