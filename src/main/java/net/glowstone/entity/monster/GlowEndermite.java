package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;

public class GlowEndermite extends GlowMonster implements Endermite {

    private boolean playerSpawned;

    public GlowEndermite(Location loc) {
        super(loc, EntityType.ENDERMITE);
    }

    public boolean isPlayerSpawned() {
        return playerSpawned;
    }

    public void setPlayerSpawned(boolean playerSpawned) {
        this.playerSpawned = playerSpawned;
    }
}
