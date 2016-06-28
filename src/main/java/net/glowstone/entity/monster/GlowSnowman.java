package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;

public class GlowSnowman extends GlowMonster implements Snowman {

    public GlowSnowman(Location loc) {
        super(loc, EntityType.SNOWMAN, 4);
    }

    @Override
    public boolean isDerp() {
        return metadata.getBit(MetadataIndex.SNOWMAN_NOHAT, 0x1);
    }

    @Override
    public void setDerp(boolean derp) {
        metadata.setBit(MetadataIndex.SNOWMAN_NOHAT, 0x1, derp);
    }
}
