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
        if (compound.containsKey("powered")) {
            entity.setPowered(compound.getBool("powered"));
        } else {
            entity.setPowered(false);
        }

        if (compound.isInt("ExplosionRadius")) {
            entity.setExplosionRadius(compound.getInt("ExplosionRadius"));
        } else {
            entity.setExplosionRadius(3);
        }

        if (compound.isInt("Fuse")) {
            entity.setMaxFuseTicks(compound.getInt("Fuse"));
        } else {
            entity.setMaxFuseTicks(30);
        }

        if (compound.containsKey("ignited")) {
            entity.setIgnited(compound.getBool("ignited"));
        } else {
            entity.setIgnited(false);
        }

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
