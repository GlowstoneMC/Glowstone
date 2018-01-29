package net.glowstone.entity;

import com.flowpowered.network.Message;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public class GlowProjectile extends GlowEntity implements Projectile {
    private boolean bounce;
    private ProjectileSource projectileSource;

    public GlowProjectile(Location location) {
        super(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Lists.newArrayList();
    }

    @Override
    public ProjectileSource getShooter() {
        return projectileSource;
    }

    @Override
    public void setShooter(ProjectileSource projectileSource) {
        this.projectileSource = projectileSource;
    }

    @Override
    public boolean doesBounce() {
        return this.bounce;
    }

    @Override
    public void setBounce(boolean bounce) {
        this.bounce = bounce;
    }
}
