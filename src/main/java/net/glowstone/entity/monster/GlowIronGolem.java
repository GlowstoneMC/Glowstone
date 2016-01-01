package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class GlowIronGolem extends GlowMonster implements IronGolem {

    private boolean playerCreated;

    public GlowIronGolem(Location loc) {
        super(loc, EntityType.IRON_GOLEM);
    }

    public GlowIronGolem(Location loc, boolean playerCreated) {
        this(loc);
        this.playerCreated = playerCreated;
    }

    @Override
    public boolean isPlayerCreated() {
        return playerCreated;
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        this.playerCreated = playerCreated;
    }
}
