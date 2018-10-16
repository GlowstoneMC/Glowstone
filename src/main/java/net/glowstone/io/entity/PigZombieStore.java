package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowPigZombie;
import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.util.UuidUtils;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

class PigZombieStore extends ZombieStore<GlowPigZombie> {

    public PigZombieStore() {
        super(GlowPigZombie.class, EntityType.PIG_ZOMBIE, GlowPigZombie::new);
    }

    @Override
    public void load(GlowZombie entity, CompoundTag tag) {
        super.load(entity, tag);
        final GlowPigZombie pigEntity = (GlowPigZombie) entity;
        pigEntity.setAnger(tag.tryGetInt("Anger").orElse(0));
        if (!tag.readString("HurtBy", uuid -> pigEntity.setHurtBy(UuidUtils.fromString(uuid)))) {
            pigEntity.setHurtBy(null);
        }
    }

    @Override
    public void save(GlowZombie entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Anger", ((GlowPigZombie) entity).getAnger());
        if (((GlowPigZombie) entity).getHurtBy() != null) {
            tag.putString("HurtBy", UuidUtils.toString(((GlowPigZombie) entity).getHurtBy()));
        } else {
            if (tag.containsKey("HurtBy")) {
                tag.remove("HurtBy");
            }
        }
    }

}
