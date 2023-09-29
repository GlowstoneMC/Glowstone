package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class GlowIronGolem extends GlowMonster implements IronGolem {

    public GlowIronGolem(Location loc) {
        super(loc, EntityType.IRON_GOLEM, 100);
        setBoundingBox(1.4, 2.7);
    }

    public GlowIronGolem(Location loc, boolean playerCreated) {
        this(loc);
        setPlayerCreated(playerCreated);
    }

    @Override
    public boolean isPlayerCreated() {
        return metadata.getByte(MetadataIndex.GOLEM_PLAYER_BUILT) == 1;
    }

    @Override
    public void setPlayerCreated(boolean playerCreated) {
        metadata.set(MetadataIndex.GOLEM_PLAYER_BUILT, playerCreated ? (byte) 1 : (byte) 0);
    }

    @Override
    public Sound getDeathSound() {
        return Sound.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    public Sound getHurtSound() {
        return Sound.ENTITY_IRON_GOLEM_HURT;
    }
}
