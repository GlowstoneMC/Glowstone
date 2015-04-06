package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.entity.meta.MetadataMap;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.util.List;

public class GlowSheep extends GlowAnimal implements Sheep {

    private boolean sheared = false;
    private DyeColor color = DyeColor.WHITE;

    public GlowSheep(Location location) {
        super(location, EntityType.SHEEP);
        setSize(0.9F, 1.3F);
    }

    @Override
    public boolean isSheared() {
        return sheared;
    }

    @Override
    public void setSheared(boolean sheared) {
        this.sheared = sheared;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor dyeColor) {
        this.color = dyeColor;
    }

    @Override
    public List<Message> createSpawnMessage() {
        List<Message> messages = super.createSpawnMessage();
        MetadataMap map = new MetadataMap(GlowSheep.class);

        map.set(MetadataIndex.SHEEP_DATA, getColorByte());
        messages.add(new EntityMetadataMessage(id, map.getEntryList()));
        return messages;
    }

    private byte getColorByte() {
        return (byte) (this.getColor().getData() & (sheared ? 0x10 : 0x0F));
    }
}
