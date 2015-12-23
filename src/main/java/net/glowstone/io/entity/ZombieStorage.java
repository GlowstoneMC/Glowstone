package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

public class ZombieStorage extends MonsterStore<GlowZombie> {

    public ZombieStorage() {
        super(GlowZombie.class, "Zombie");
    }

    @Override
    public GlowZombie createEntity(Location location, CompoundTag compound) {
        return new GlowZombie(location);
    }

    @Override
    public void load(GlowZombie entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setVillager(compound.getBool("IsVillager"));
        entity.setBaby(compound.getBool("IsBaby"));
    }

    @Override
    public void save(GlowZombie entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("IsVillager", entity.isVillager());
        tag.putBool("IsBaby", entity.isBaby());
    }

}
