package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
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

public class GlowZombie extends GlowMonster implements Zombie {

    @Setter
    private boolean canBreakDoors;
    @Getter
    @Setter
    private int age;
    private boolean canBreed;
    @Setter
    private boolean ageLock;
    private boolean shouldBurnInDay = true;

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
     * @param loc  the location
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
        throw new IllegalArgumentException("Zombies cannot be villagers.");
    }

    @Override
    public Profession getVillagerProfession() {
        return null;
    }

    @Override
    public void setVillagerProfession(Profession profession) {
        //Field has been removed as of 1.11
    }

    @Override
    public boolean isConverting() {
        // TODO: 1.13 zombie API
        return false;
    }

    @Override
    public int getConversionTime() {
        return 0;
    }

    @Override
    public void setConversionTime(int time) {

    }

    @Override
    public boolean isDrowning() {
        return false;
    }

    @Override
    public void startDrowning(int drownedConversionTime) {

    }

    @Override
    public void stopDrowning() {

    }

    @Override
    public boolean isArmsRaised() {
        return false;
    }

    @Override
    public void setArmsRaised(boolean raised) {

    }

    @Override
    public boolean shouldBurnInDay() {
        return shouldBurnInDay;
    }

    @Override
    public void setShouldBurnInDay(boolean shouldBurnInDay) {
        this.shouldBurnInDay = shouldBurnInDay;
    }

    @Override
    public boolean canBreakDoors() {
        return canBreakDoors;
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

    @Override
    public void setBaby() {
        this.setBaby(true);
    }

    @Override
    public void setAdult() {
        this.setBaby(false);
    }

    @Override
    public boolean isAdult() {
        return !this.isBaby();
    }

    @Override
    public boolean canBreed() {
        return canBreed;
    }

    @Override
    public void setBreed(boolean b) {
        this.canBreed = b;
    }

    @Override
    public boolean getAgeLock() {
        return ageLock;
    }
}
