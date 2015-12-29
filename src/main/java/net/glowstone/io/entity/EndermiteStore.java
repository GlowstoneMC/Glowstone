package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEndermite;
import net.glowstone.util.nbt.CompoundTag;

public class EndermiteStore extends MonsterStore<GlowEndermite> {

    public EndermiteStore() {
        super(GlowEndermite.class, "Endermite");
    }

    @Override
    public void load(GlowEndermite entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setTicksLived(compound.getInt("Lifetime"));
        entity.setPlayerSpawned(compound.getBool("PlayerSpawned"));
    }

    @Override
    public void save(GlowEndermite entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putInt("Lifetime", entity.getTicksLived());
        compound.putBool("PlayerSpawned", entity.isPlayerSpawned());
    }

}
