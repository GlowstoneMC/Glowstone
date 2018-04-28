package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowVindicator;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public class VindicatorStore extends MonsterStore<GlowVindicator> {

    public VindicatorStore() {
        super(GlowVindicator.class, EntityType.VINDICATOR, GlowVindicator::new);
    }

    @Override
    public void load(GlowVindicator entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readBoolean("Johnny", entity::setJohnny);
    }

    @Override
    public void save(GlowVindicator entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("Johnny", entity.isJohnny());
    }
}
