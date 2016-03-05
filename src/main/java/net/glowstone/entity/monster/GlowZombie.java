package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;

import java.util.List;

public class GlowZombie extends GlowMonster implements Zombie {

    private int conversionTime = -1;
    private boolean canBreakDoors;

    public GlowZombie(Location loc) {
        this(loc, EntityType.ZOMBIE);
    }

    public GlowZombie(Location loc, EntityType type) {
        super(loc, type, 20);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.ZOMBIE_IS_CONVERTING, conversionTime > 0 ? (byte) 1 : (byte) 0);
        return super.createSpawnMessage();
    }

    @Override
    public boolean isBaby() {
        return metadata.getByte(MetadataIndex.ZOMBIE_IS_CHILD) == 1;
    }

    @Override
    public void setBaby(boolean value) {
        metadata.set(MetadataIndex.ZOMBIE_IS_CHILD, value ? (byte) 1 : (byte) 0);
    }

    @Override
    public boolean isVillager() {
        return metadata.getByte(MetadataIndex.ZOMBIE_IS_VILLAGER) == 1;
    }

    @Override
    public void setVillager(boolean value) {
        metadata.set(MetadataIndex.ZOMBIE_IS_VILLAGER, value ? (byte) 1 : (byte) 0);
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {

    }

    @Override
    public Villager.Profession getVillagerProfession() {
        return null;
    }

    public int getConversionTime() {
        return conversionTime;
    }

    public void setConversionTime(int conversionTime) {
        this.conversionTime = conversionTime;
        metadata.set(MetadataIndex.ZOMBIE_IS_CONVERTING, conversionTime > 0 ? (byte) 1 : (byte) 0);
    }

    public boolean isCanBreakDoors() {
        return canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        this.canBreakDoors = canBreakDoors;
    }
}
