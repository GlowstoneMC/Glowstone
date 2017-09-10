package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;

public class GlowEndermite extends GlowMonster implements Endermite {

    private boolean playerSpawned;

    public GlowEndermite(Location loc) {
        super(loc, EntityType.ENDERMITE, 8);
        setBoundingBox(0.4, 0.3);
    }

    public boolean isPlayerSpawned() {
        return playerSpawned;
    }

    public void setPlayerSpawned(boolean playerSpawned) {
        this.playerSpawned = playerSpawned;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERMITE_DEATH;
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ENDERMITE_HURT;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ENDERMITE_AMBIENT;
    }

    @Override
    public boolean isArthropod() {
        return true;
    }
}
