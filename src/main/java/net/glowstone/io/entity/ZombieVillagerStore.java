package net.glowstone.io.entity;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.entity.monster.GlowZombie;
import net.glowstone.entity.monster.GlowZombieVillager;
import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class ZombieVillagerStore extends ZombieStore<GlowZombieVillager> {

    private static final Villager.Profession[] PROFESSIONS = Villager.Profession.values();

    public ZombieVillagerStore() {
        super(GlowZombieVillager.class, EntityType.ZOMBIE_VILLAGER);
    }

    @Override
    public void load(GlowZombie zombie, CompoundTag compound) {
        checkArgument(zombie instanceof GlowZombieVillager);
        GlowZombieVillager entity = (GlowZombieVillager) zombie;
        super.load(entity, compound);

        if (compound.isInt("Profession")) {
            int professionId = compound.getInt("Profession");
            if (GlowVillager.isValidProfession(professionId)) {
                entity.setVillagerProfession(GlowVillager.getProfessionById(professionId));
            } else {
                entity.setVillagerProfession(GlowVillager.getRandomProfession(ThreadLocalRandom.current()));
            }
        } else {
            entity.setVillagerProfession(GlowVillager.getRandomProfession(ThreadLocalRandom.current()));
        }

        if (compound.isInt("ConversionTime")) {
            entity.setConversionTime(compound.getInt("ConversionTime"));
        } else {
            entity.setConversionTime(-1);
        }

        if (compound.isLong("ConversionPlayerMost") && compound.isLong("ConversionPlayerLeast")) {
            UUID conversionPlayer = new UUID(compound.getLong("ConversionPlayerMost"),
                    compound.getLong("ConversionPlayerLeast"));
            entity.setConversionPlayer(conversionPlayer);
        }
    }

    @Override
    public void save(GlowZombie zombie, CompoundTag compound) {
        checkArgument(zombie instanceof GlowZombieVillager);
        GlowZombieVillager entity = (GlowZombieVillager) zombie;
        super.save(entity, compound);

        if (entity.getVillagerProfession() != null && entity.getVillagerProfession() != Villager.Profession.HUSK) {
            compound.putInt("Profession", entity.getVillagerProfession().ordinal());
        }

        compound.putInt("ConversionTime", entity.getConversionTime());

        if (entity.getConversionPlayer() != null) {
            compound.putLong("ConversionPlayerMost", entity.getConversionPlayer().getMostSignificantBits());
            compound.putLong("ConversionPlayerLeast", entity.getConversionPlayer().getLeastSignificantBits());
        }
    }
}
