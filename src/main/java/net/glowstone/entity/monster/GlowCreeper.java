package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

public class GlowCreeper extends GlowMonster implements Creeper {
    private boolean powered = false;
    private int explosionRadius;
    private int fuse;
    private boolean ignited;

    public GlowCreeper(Location loc) {
        super(loc, EntityType.CREEPER);
    }

    @Override
    public boolean isPowered() {
        return this.powered;
    }

    @Override
    public void setPowered(boolean value) {
        this.powered = value;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }

    public void setExplosionRadius(int explosionRadius) {
        this.explosionRadius = explosionRadius;
    }

    public int getFuse() {
        return fuse;
    }

    public void setFuse(int fuse) {
        this.fuse = fuse;
    }

    public boolean isIgnited() {
        return ignited;
    }

    public void setIgnited(boolean ignited) {
        this.ignited = ignited;
    }
}
