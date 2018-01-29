package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowIronGolem;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class IronGolemStore extends MonsterStore<GlowIronGolem> {

    public IronGolemStore() {
        super(GlowIronGolem.class, EntityType.IRON_GOLEM, GlowIronGolem::new);
    }

    @Override
    public void load(GlowIronGolem entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isByte("PlayerCreated")) {
            entity.setPlayerCreated(compound.getBool("PlayerCreated"));
        } else {
            entity.setPlayerCreated(true);
        }

    }

    @Override
    public void save(GlowIronGolem entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("PlayerCreated", entity.isPlayerCreated());
    }
}
