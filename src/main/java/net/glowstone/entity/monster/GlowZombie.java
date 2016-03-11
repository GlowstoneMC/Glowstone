package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;
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
        metadata.set(MetadataIndex.ZOMBIE_IS_CONVERTING, conversionTime > 0);
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
        return metadata.getInt(MetadataIndex.ZOMBIE_IS_VILLAGER) > 0;
    }

    @Override
    public void setVillager(boolean value) {
        metadata.set(MetadataIndex.ZOMBIE_IS_VILLAGER, value ? 1 : 0);
    }

    @Override
    public void setVillagerProfession(Profession profession) {
        metadata.set(MetadataIndex.ZOMBIE_IS_VILLAGER, profession.getId() + 1);
    }

    @Override
    public Profession getVillagerProfession() {
        return Profession.getProfession(metadata.getInt(MetadataIndex.ZOMBIE_IS_VILLAGER) - 1);
    }

    public int getConversionTime() {
        return conversionTime;
    }

    public void setConversionTime(int conversionTime) {
        this.conversionTime = conversionTime;
        metadata.set(MetadataIndex.ZOMBIE_IS_CONVERTING, conversionTime > 0);
    }

    public boolean isCanBreakDoors() {
        return canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        this.canBreakDoors = canBreakDoors;
    }
}
