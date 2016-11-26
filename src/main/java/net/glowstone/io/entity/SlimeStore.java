package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowSlime;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class SlimeStore<T extends GlowSlime> extends MonsterStore<GlowSlime> {

    public SlimeStore(Class<GlowSlime> clazz, EntityType type) {
        super(clazz, type);
    }

    @Override
    public void load(GlowSlime entity, CompoundTag tag) {
        super.load(entity, tag);
        if (tag.isInt("Size")) {
            entity.setSize(tag.getInt("Size"));
        } else {
            entity.setSize(1);
        }

        if (tag.isByte("wasOnGround")) {
            entity.setOnGround(tag.getBool("wasOnGround"));
        } else {
            entity.setOnGround(false);
        }
    }

    @Override
    public void save(GlowSlime entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Size", entity.getSize());
        tag.putBool("wasOnGround", entity.isOnGround());
    }

}
