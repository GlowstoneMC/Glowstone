package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowGuardian;
import net.glowstone.util.nbt.CompoundTag;

class GuardianStore extends MonsterStore<GlowGuardian> {

    public GuardianStore() {
        super(GlowGuardian.class, "Guardian");
    }

    @Override
    public void load(GlowGuardian entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isByte("Elder")) {
            entity.setElder(compound.getBool("Elder"));
        } else {
            entity.setElder(false);
        }

    }

    @Override
    public void save(GlowGuardian entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putBool("Elder", entity.isElder());
    }

}
