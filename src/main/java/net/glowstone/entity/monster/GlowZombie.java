package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

public class GlowZombie extends GlowMonster implements Zombie {
    private boolean baby = false;
    private boolean villager = false;
    
    public GlowZombie(Location loc) {
        super(loc, EntityType.ZOMBIE);
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
}
