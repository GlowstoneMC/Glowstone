package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;

class ZombieStore<T extends GlowZombie> extends MonsterStore<GlowZombie> {

    public ZombieStore() {
        super(GlowZombie.class, "Zombie");
    }

    public ZombieStore(Class clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(GlowZombie entity, CompoundTag tag) {
        super.load(entity, tag);
        entity.setVillager(tag.getBool("IsVillager"));
        entity.setBaby(tag.getBool("IsBaby"));
        entity.setConversionTime(tag.getInt("ConversionTime"));
        entity.setCanBreakDoors(tag.getBool("CanBreakDoors"));
    }

    @Override
    public void save(GlowZombie entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("IsVillager", entity.isVillager());
        tag.putBool("IsBaby", entity.isBaby());
        tag.putInt("ConversionTime", entity.getConversionTime());
        tag.putBool("CanBreakDoors", entity.isCanBreakDoors());
    }
}
