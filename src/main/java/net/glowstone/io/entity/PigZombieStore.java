package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowPigZombie;
import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.nbt.CompoundTag;

import java.util.UUID;

public class PigZombieStore extends ZombieStore<GlowPigZombie> {

    public PigZombieStore() {
        super(GlowPigZombie.class, "PigZombie");
    }

    @Override
    public void load(GlowZombie entity, CompoundTag tag) {
        super.load(entity, tag);
        ((GlowPigZombie) entity).setAnger(tag.getInt("Anger"));
        ((GlowPigZombie) entity).setHurtBy(UUID.fromString(tag.getString("HurtBy")));
    }

    @Override
    public void save(GlowZombie entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Anger", ((GlowPigZombie) entity).getAnger());
        tag.putString("HurtBy", ((GlowPigZombie) entity).getHurtBy().toString());
    }

}
