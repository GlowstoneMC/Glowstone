package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowVex;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public class VexStore extends MonsterStore<GlowVex> {

    public VexStore() {
        super(GlowVex.class, EntityType.VEX, GlowVex::new);
    }

    @Override
    public void load(GlowVex entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isInt("LifeTicks")) {
            entity.setLifeTicks(tag.getInt("LifeTicks"));
        }
    }

    @Override
    public void save(GlowVex entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("LifeTicks", entity.getLifeTicks());
    }
}
