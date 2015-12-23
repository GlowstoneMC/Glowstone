package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSlime;
import net.glowstone.util.nbt.CompoundTag;

public class SlimeStore extends MonsterStore<GlowSlime> {

    public SlimeStore() {
        super(GlowSlime.class, "Slime");
    }

    @Override
    public void load(GlowSlime entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setSize(compound.getInt("Size"));
    }

    @Override
    public void save(GlowSlime entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Size", entity.getSize());
    }

}
