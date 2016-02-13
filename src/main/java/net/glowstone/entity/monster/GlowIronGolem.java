package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;

import java.util.List;

public class GlowIronGolem extends GlowMonster implements IronGolem {

    public GlowIronGolem(Location loc) {
        super(loc, EntityType.IRON_GOLEM);
    }

    public GlowIronGolem(Location loc, boolean playerCreated) {
        this(loc);
        setPlayerCreated(playerCreated);
        setMaxHealthAndHealth(100);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.GOLEM_PLAYER_BUILT, isPlayerCreated() ? (byte) 1 : (byte) 0);
        return super.createSpawnMessage();
    }

    @Override
    public List<Message> createUpdateMessage() {
        return super.createUpdateMessage();
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
