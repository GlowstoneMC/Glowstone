package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;

import java.util.Random;

public class GlowSheep extends GlowAnimal implements Sheep {

    private boolean sheared = false;
    private DyeColor color = DyeColor.WHITE;

    public GlowSheep(Location location) {
        super(location, EntityType.SHEEP, 8);
        setSize(0.9F, 1.3F);
        Random r = new Random();
        int colorpc = r.nextInt(10000);
        if(colorpc < 8184) {
            setColor(DyeColor.WHITE);
        } else if (colorpc >= 8184 && 8684 > colorpc) {
            setColor(DyeColor.BLACK);
        } else if (colorpc >= 8684 && 9184 > colorpc) {
            setColor(DyeColor.SILVER);
        } else if (colorpc >= 9184 && 9684 > colorpc) {
            setColor(DyeColor.GRAY);
        } else if (colorpc >= 9684 && 9984 > colorpc) {
            setColor(DyeColor.BROWN);
        } else {
            setColor(DyeColor.PINK);
        }
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

    private byte getColorByte() {
        return (byte) (this.getColor().getData() & (sheared ? 0x10 : 0x0F));
    }
}
