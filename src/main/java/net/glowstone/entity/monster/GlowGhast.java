package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

import java.util.List;

public class GlowGhast extends GlowMonster implements Ghast {

    private int explosionPower;

    public GlowGhast(Location loc) {
        super(loc, EntityType.GHAST);
        setMaxHealthAndHealth(10);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.GHAST_ATTACKING, isAttacking() ? (byte) 1 : (byte) 0);
        return super.createSpawnMessage();
    }

    public int getExplosionPower() {
        return explosionPower;
    }

    public void setExplosionPower(int explosionPower) {
        this.explosionPower = explosionPower;
    }

    public boolean isAttacking() {
        return metadata.getByte(MetadataIndex.GHAST_ATTACKING) == 1;
    }

    public void setAttacking(boolean attacking) {
        metadata.set(MetadataIndex.GHAST_ATTACKING, attacking ? (byte) 1 : (byte) 0);
    }
}
