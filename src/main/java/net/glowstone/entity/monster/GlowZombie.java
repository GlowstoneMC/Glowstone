package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import net.glowstone.entity.ai.EntityDirector;
import net.glowstone.entity.ai.HostileMobState;
import net.glowstone.entity.ai.MobState;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.util.SoundUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Zombie;

import java.util.List;

public class GlowZombie extends GlowMonster implements Zombie {

    private boolean canBreakDoors;

    /**
     * Creates a zombie.
     *
     * @param loc the location
     */
    public GlowZombie(Location loc) {
        this(loc, EntityType.ZOMBIE);
    }

    /**
     * Creates a zombie.
     *
     * @param loc the location
     * @param type the zombie type
     */
    public GlowZombie(Location loc, EntityType type) {
        super(loc, type, 20);
        setBoundingBox(0.6, 1.8);
        if (type != null) {
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_around");
            EntityDirector.registerEntityMobState(type, MobState.IDLE, "look_player");
            EntityDirector.registerEntityMobState(type, HostileMobState.TARGETING, "follow_player");
        }
        setState(MobState.IDLE);
    }

    @Override
    public List<Message> createSpawnMessage() {
        //TODO - 1.11 Move this to ZombieVillager
        //metadata.set(MetadataIndex.ZOMBIE_IS_CONVERTING, conversionTime > 0);
        return super.createSpawnMessage();
    }

    @Override
    public boolean isBaby() {
        return metadata.getBoolean(MetadataIndex.ZOMBIE_IS_CHILD);
    }

    @Override
    public void setBaby(boolean value) {
        metadata.set(MetadataIndex.ZOMBIE_IS_CHILD, value);
    }

    @Override
    public boolean isVillager() {
        return false;
    }

    @Override
    public void setVillager(boolean value) {
        //Field has been removed as of 1.11
    }

    @Override
    public Profession getVillagerProfession() {
        //Field has been removed as of 1.11
        return Profession.NORMAL;
    }

    @Override
    public void setVillagerProfession(Profession profession) {
        //Field has been removed as of 1.11
    }

    public boolean isCanBreakDoors() {
        return canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        this.canBreakDoors = canBreakDoors;
    }

    @Override
    protected float getSoundPitch() {
        if (isBaby()) {
            return SoundUtil.randomReal(0.2F) + 1.5F;
        }
        return super.getSoundPitch();
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ZOMBIE_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ZOMBIE_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ZOMBIE_AMBIENT;
    }

    @Override
    public boolean isUndead() {
        return true;
    }
}
