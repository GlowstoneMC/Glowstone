package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSlime;
import net.glowstone.util.nbt.CompoundTag;

class SlimeStore<T extends GlowSlime> extends MonsterStore<GlowSlime> {

    public SlimeStore(Class<GlowSlime> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(GlowSlime entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setSize(tag.getInt("Size"));
        entity.setOnGround(tag.getBool("wasOnGround"));
    }

    @Override
    public void save(GlowSlime entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Size", entity.getSize());
        tag.putBool("wasOnGround", entity.isOnGround());
    }

}
