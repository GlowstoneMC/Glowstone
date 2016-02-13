package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Spider;

import java.util.List;

public class GlowSpider extends GlowMonster implements Spider {

    public GlowSpider(Location loc) {
        super(loc, EntityType.SPIDER);
        setMaxHealthAndHealth(16);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.SPIDER_CLIMBING, isClimbing() ? (byte) 1 : (byte) 0);
        return super.createSpawnMessage();
    }

    public boolean isClimbing() {
        return metadata.getByte(MetadataIndex.SPIDER_CLIMBING) == 1;
    }

    public void setClimbing(boolean climbing) {
        metadata.set(MetadataIndex.SPIDER_CLIMBING, climbing ? (byte) 1 : (byte) 0);
    }
}
