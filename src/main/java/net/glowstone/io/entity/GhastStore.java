package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowGhast;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class GhastStore extends MonsterStore<GlowGhast> {

    public GhastStore() {
        super(GlowGhast.class, EntityType.GHAST, GlowGhast::new);
    }

    @Override
    public void load(GlowGhast entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setExplosionPower(compound.tryGetInt("ExplosionPower").orElse(1));
    }

    @Override
    public void save(GlowGhast entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putInt("ExplosionPower", entity.getExplosionPower());
    }

}
