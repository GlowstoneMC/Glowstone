package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEndermite;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class EndermiteStore extends MonsterStore<GlowEndermite> {

    public EndermiteStore() {
        super(GlowEndermite.class, EntityType.ENDERMITE, GlowEndermite::new);
    }

    @Override
    public void load(GlowEndermite entity, CompoundTag compound) {
        super.load(entity, compound);
        if (!compound.readInt(entity::setTicksLived, "Lifetime")) {
            entity.setTicksLived(1);
        }

        entity.setPlayerSpawned(compound.getBoolDefaultTrue("PlayerSpawned"));
    }

    @Override
    public void save(GlowEndermite entity, CompoundTag compound) {
        super.save(entity, compound);
        compound.putInt("Lifetime", entity.getTicksLived());
        compound.putBool("PlayerSpawned", entity.isPlayerSpawned());
    }

}
