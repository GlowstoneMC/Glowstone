package net.glowstone.entity.passive;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
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
        setMaxHealthAndHealth(8);
    }

    @Override
    public boolean isSheared() {
        return sheared;
    }

    @Override
    public void setSheared(boolean sheared) {
        this.sheared = sheared;
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(DyeColor dyeColor) {
        this.color = dyeColor;
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.SHEEP_DATA, getColorByte());
        return super.createSpawnMessage();
    }

    private byte getColorByte() {
        return (byte) (this.getColor().getData() & (sheared ? 0x10 : 0x0F));
    }
}
