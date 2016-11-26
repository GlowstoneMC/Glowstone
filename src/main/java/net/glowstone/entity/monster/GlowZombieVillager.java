package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

public class GlowZombieVillager extends GlowZombie implements ZombieVillager {

    public GlowZombieVillager(Location loc) {
        super(loc, EntityType.ZOMBIE_VILLAGER);
        setBoundingBox(0.6, 1.95);
    }

    @Override
    public Villager.Profession getVillagerProfession() {
        int profession = metadata.containsKey(MetadataIndex.ZOMBIE_VILLAGER_PROFESSION) ? metadata.getInt(MetadataIndex.ZOMBIE_VILLAGER_PROFESSION) : 0;
        return Villager.Profession.values()[profession];
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {
        metadata.set(MetadataIndex.ZOMBIE_VILLAGER_PROFESSION, profession.ordinal());
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }
}
