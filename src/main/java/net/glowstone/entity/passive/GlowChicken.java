package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;

public class GlowChicken extends GlowAnimal implements Chicken {

    private boolean chickenJockey;
    private int eggLayTime;

    public GlowChicken(Location location) {
        super(location, EntityType.CHICKEN, 4);
        setSize(0.4F, 0.7F);
    }

    public boolean isChickenJockey() {
        return chickenJockey;
    }

    public void setChickenJockey(boolean chickenJockey) {
        this.chickenJockey = chickenJockey;
    }

    public int getEggLayTime() {
        return eggLayTime;
    }

    public void setEggLayTime(int eggLayTime) {
        this.eggLayTime = eggLayTime;
    }
}
