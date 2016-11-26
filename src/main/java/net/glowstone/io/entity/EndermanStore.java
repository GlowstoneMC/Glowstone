package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEnderman;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class EndermanStore extends MonsterStore<GlowEnderman> {

    public EndermanStore() {
        super(GlowEnderman.class, EntityType.ENDERMAN);
    }

    @Override
    public void load(GlowEnderman entity, CompoundTag compound) {
        super.load(entity, compound);
        //TODO: Block
    }

    @Override
    public void save(GlowEnderman entity, CompoundTag tag) {
        super.save(entity, tag);
        //TODO: Block
    }
}
