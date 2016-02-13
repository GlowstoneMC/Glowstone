package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

public class GlowIronGolem extends GlowMonster implements IronGolem {

    public GlowIronGolem(Location loc) {
        super(loc, EntityType.IRON_GOLEM, 100);
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
        metadata.set(MetadataIndex.GOLEM_PLAYER_BUILT, playerCreated);
    }
}
