package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowWither;
import net.glowstone.util.nbt.CompoundTag;

public class WitherStore extends MonsterStore<GlowWither> {
    public WitherStore() {
        super(GlowWither.class, "WitherBoss");
    }

    @Override
    public void load(GlowWither entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setInvulnerableTicks(tag.getInt("Invul"));
    }

    @Override
    public void save(GlowWither entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Invul", entity.getInvulnerableTicks());
    }
}
