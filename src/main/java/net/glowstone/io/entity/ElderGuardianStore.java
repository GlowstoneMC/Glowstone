package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowElderGuardian;
import net.glowstone.entity.monster.GlowGuardian;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class ElderGuardianStore extends MonsterStore<GlowElderGuardian> {

    public ElderGuardianStore() {
        super(GlowElderGuardian.class, EntityType.ELDER_GUARDIAN, GlowElderGuardian::new);
    }

    @Override
    public void load(GlowElderGuardian entity, CompoundTag compound) {
        super.load(entity, compound);
    }

    @Override
    public void save(GlowElderGuardian entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putBool("Elder", true);
    }

}
