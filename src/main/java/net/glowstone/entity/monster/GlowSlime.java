package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

import java.util.List;
import java.util.Random;

public class GlowSlime extends GlowMonster implements Slime {

    private int size;

    public GlowSlime(Location loc) {
        super(loc, EntityType.SLIME);
        Random rand = new Random();
        this.size = rand.nextInt(3) + 1;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int sz) {
        this.size = sz;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowSlime.class);
        map.set(MetadataIndex.SLIME_SIZE, getSize());
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

}
