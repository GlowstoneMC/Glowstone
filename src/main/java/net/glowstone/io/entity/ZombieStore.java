package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.Villager;

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

        if (tag.isByte("IsVillager")) {
            entity.setVillager(tag.getBool("IsVillager"));
        } else {
            entity.setVillager(false);
        }

        if (tag.isByte("IsBaby")) {
            entity.setBaby(tag.getBool("IsBaby"));
        } else {
            entity.setBaby(false);
        }

        if (tag.isInt("ZombieType")) {
            entity.setVillagerProfession(Villager.Profession.values()[tag.getInt("ZombieType")]);
        }

        if (tag.isInt("VillagerProfession")) { // Legacy (< 1.10)
            entity.setVillagerProfession(Villager.Profession.values()[tag.getInt("VillagerProfession")]);
        }

        if (tag.isInt("ConversionTime")) {
            entity.setConversionTime(tag.getInt("ConversionTime"));
        } else {
            entity.setConversionTime(-1);
        }

        if (tag.isByte("CanBreakDoors")) {
            entity.setCanBreakDoors(tag.getBool("CanBreakDoors"));
        } else {
            entity.setCanBreakDoors(true);
        }

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
