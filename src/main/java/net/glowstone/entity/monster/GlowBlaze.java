package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;

import java.util.List;

public class GlowBlaze extends GlowMonster implements Blaze {

    public GlowBlaze(Location loc) {
        super(loc, EntityType.BLAZE);
        setMaxHealthAndHealth(20);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.BLAZE_ON_FIRE, isOnFire() ? (byte) 1 : (byte) 0);
        return super.createSpawnMessage();
    }

    @Override
    public List<Message> createUpdateMessage() {
        return super.createUpdateMessage();
    }

    public boolean isOnFire() {
        return metadata.getByte(MetadataIndex.BLAZE_ON_FIRE) == 1;
    }

    public void setOnFire(boolean onFire) {
        metadata.set(MetadataIndex.BLAZE_ON_FIRE, onFire ? (byte) 1 : (byte) 0);
    }
}
