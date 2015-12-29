package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowIronGolem;
import net.glowstone.util.nbt.CompoundTag;

public class IronGolemStore extends MonsterStore<GlowIronGolem> {

    public IronGolemStore() {
        super(GlowIronGolem.class, "VillagerGolem");
    }

    @Override
    public void load(GlowIronGolem entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setPlayerCreated(compound.getBool("PlayerCreated"));
    }

    @Override
    public void save(GlowIronGolem entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("PlayerCreated", entity.isPlayerCreated());
    }
}
