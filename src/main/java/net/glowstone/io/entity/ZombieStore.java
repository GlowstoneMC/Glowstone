package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class ZombieStore<T extends GlowZombie> extends MonsterStore<GlowZombie> {

    public ZombieStore() {
        super(GlowZombie.class, EntityType.ZOMBIE, GlowZombie::new);
    }

    public ZombieStore(Class<T> clazz, EntityType type, Function<Location, T> creator) {
        super(clazz, type, creator);
    }

    @Override
    public void load(GlowZombie entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isByte("IsBaby")) {
            entity.setBaby(tag.getBool("IsBaby"));
        } else {
            entity.setBaby(false);
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
        tag.putBool("IsBaby", entity.isBaby());
        tag.putBool("CanBreakDoors", entity.isCanBreakDoors());
    }
}
