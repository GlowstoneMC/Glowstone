package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class GlowZombie extends GlowMonster implements Zombie {

    private boolean baby;
    private boolean villager;
    private int conversionTime = -1;
    private boolean canBreakDoors;
    
    public GlowZombie(Location loc) {
        super(loc, EntityType.ZOMBIE);
    }

    public GlowZombie(Location loc, EntityType type) {
        super(loc, type);
    }
    
    @Override
    public boolean isBaby() {
        return this.baby;
    }
    
    @Override
    public void setBaby(boolean value) {
        this.baby = value;
    }
    
    @Override
    public boolean isVillager() {
        return this.villager;
    }
    
    @Override
    public void setVillager(boolean value) {
        this.villager = value;
    }

    public int getConversionTime() {
        return conversionTime;
    }

    public void setConversionTime(int conversionTime) {
        this.conversionTime = conversionTime;
    }

    public boolean isCanBreakDoors() {
        return canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        this.canBreakDoors = canBreakDoors;
    }
}
