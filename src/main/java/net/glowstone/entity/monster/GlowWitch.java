package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class GlowWitch extends GlowMonster implements Witch {

    public GlowWitch(Location loc) {
        super(loc, EntityType.WITCH, 26);
    }

    public boolean isAggressive() {
        return metadata.getBoolean(MetadataIndex.WITCH_AGGRESSIVE);
    }

    public void setAggressive(boolean aggressive) {
        metadata.set(MetadataIndex.WITCH_AGGRESSIVE, aggressive);
    }
}
