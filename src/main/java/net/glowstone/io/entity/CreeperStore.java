package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowCreeper;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class CreeperStore extends MonsterStore<GlowCreeper> {

    public CreeperStore() {
        super(GlowCreeper.class, EntityType.CREEPER, GlowCreeper::new);
    }

    @Override
    public void load(GlowCreeper entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.readBoolean("powered", entity::setPowered);
        if (!compound.readInt("ExplosionRadius", entity::setExplosionRadius)) {
            entity.setExplosionRadius(3);
        }
        if (!compound.readInt("Fuse", entity::setMaxFuseTicks)) {
            entity.setMaxFuseTicks(30);
        }
        entity.setIgnited(compound.getBoolDefaultFalse("ignited"));
    }

    @Override
    public void save(GlowCreeper entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("powered", entity.isPowered());
        tag.putInt("ExplosionRadius", entity.getExplosionRadius());
        tag.putInt("Fuse", entity.getMaxFuseTicks());
        tag.putBool("ignited", entity.isIgnited());
    }

}
