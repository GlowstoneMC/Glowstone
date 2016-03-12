package net.glowstone.entity.monster;

import com.flowpowered.network.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import java.util.List;

public class GlowCreeper extends GlowMonster implements Creeper {

    private int explosionRadius;
    private int fuse;
    private boolean ignited;

    public GlowCreeper(Location loc) {
        super(loc, EntityType.CREEPER, 20);
    }

    @Override
    public List<Message> createSpawnMessage() {
        // todo Implement the fuse & Ignition later.
        return super.createSpawnMessage();
    }

    @Override
    public boolean isPowered() {
        return metadata.getBoolean(MetadataIndex.CREEPER_POWERED);
    }

    @Override
    public void setPowered(boolean value) {
        metadata.set(MetadataIndex.CREEPER_POWERED, value);
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
