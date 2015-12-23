package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.util.nbt.CompoundTag;

public class CreeperStore extends MonsterStore<GlowCreeper> {

    public CreeperStore() {
        super(GlowCreeper.class, "Creeper");
    }

    @Override
    public void load(GlowCreeper entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setPowered(compound.getBool("powered"));
    }

    @Override
    public void save(GlowCreeper entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("powered", entity.isPowered());
    }
}
