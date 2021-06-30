package net.glowstone.io.entity;

import static com.google.common.base.Preconditions.checkArgument;
import static net.glowstone.entity.passive.GlowVillager.getRandomProfession;

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
        super(GlowZombieVillager.class, EntityType.ZOMBIE_VILLAGER, GlowZombieVillager::new);
    }

    @Override
    public void load(GlowZombie zombie, CompoundTag compound) {
        checkArgument(zombie instanceof GlowZombieVillager);
        GlowZombieVillager entity = (GlowZombieVillager) zombie;
        super.load(entity, compound);
        entity.setVillagerProfession(compound.tryGetInt("Profession")
            .filter(GlowVillager::isValidProfession)
            .map(GlowVillager::getProfessionById)
            .orElseGet(() -> getRandomProfession(ThreadLocalRandom.current())));
        entity.setConversionTime(compound.tryGetInt("ConversionTime").orElse(-1));
        compound.readUuid("ConversionPlayerMost", "ConversionPlayerLeast",
            entity::setConversionPlayerId);
    }

    @Override
    public void save(GlowZombie zombie, CompoundTag compound) {
        checkArgument(zombie instanceof GlowZombieVillager);
        GlowZombieVillager entity = (GlowZombieVillager) zombie;
        super.save(entity, compound);

        final Villager.Profession profession = entity.getVillagerProfession();
        if (profession != null) {
            compound.putInt("Profession", profession.ordinal());
        }

        compound.putInt("ConversionTime", entity.getConversionTime());

        final UUID conversionPlayer = entity.getConversionPlayerId();
        if (conversionPlayer != null) {
            compound.putLong("ConversionPlayerMost", conversionPlayer.getMostSignificantBits());
            compound.putLong("ConversionPlayerLeast", conversionPlayer.getLeastSignificantBits());
        }
    }
}
