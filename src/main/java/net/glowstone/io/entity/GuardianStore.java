package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowGuardian;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class GuardianStore extends MonsterStore<GlowGuardian> {

    public GuardianStore() {
        super(GlowGuardian.class, EntityType.GUARDIAN, GlowGuardian::new);
    }

    @Override
    public void load(GlowGuardian entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    @Override
    public void save(GlowGuardian entity, CompoundTag compound) {
        super.save(entity, compound);
    }

}
