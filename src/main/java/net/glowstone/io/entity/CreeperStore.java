package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.util.nbt.CompoundTag;

class CreeperStore extends MonsterStore<GlowCreeper> {

    public CreeperStore() {
        super(GlowCreeper.class, "Creeper");
    }

    @Override
    public void load(GlowCreeper entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setPowered(compound.getBool("powered"));
        entity.setExplosionRadius(compound.getInt("ExplosionRadius"));
        entity.setFuse(compound.getInt("Fuse"));
        entity.setIgnited(compound.getBool("ignited"));
    }

    @Override
    public void save(GlowCreeper entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("powered", entity.isPowered());
        tag.putInt("ExplosionRadius", entity.getExplosionRadius());
        tag.putInt("Fuse", entity.getFuse());
        tag.putBool("ignited", entity.isIgnited());
    }

}
