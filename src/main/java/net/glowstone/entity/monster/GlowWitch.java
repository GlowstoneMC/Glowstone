package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

public class GlowWitch extends GlowMonster implements Witch {

    public GlowWitch(Location loc) {
        super(loc, EntityType.WITCH, 26);
    }

    public boolean isAgressive() {
        return metadata.getByte(MetadataIndex.WITCH_AGGRESSIVE) == 1;
    }

    public void setAgressive(boolean agressive) {
        metadata.set(MetadataIndex.WITCH_AGGRESSIVE, agressive ? (byte) 1 : (byte) 0);
    }
}
