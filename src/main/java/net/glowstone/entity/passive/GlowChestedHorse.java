package net.glowstone.entity.passive;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.EntityType;

public abstract class GlowChestedHorse extends GlowAbstractHorse implements ChestedHorse {

    public GlowChestedHorse(Location location, EntityType type, double maxHealth) {
        super(location, type, maxHealth);
    }

    @Override
    public boolean isCarryingChest() {
        return metadata.getBoolean(MetadataIndex.CHESTED_HORSE_HAS_CHEST);
    }

    @Override
    public void setCarryingChest(boolean carryingChest) {
        metadata.set(MetadataIndex.CHESTED_HORSE_HAS_CHEST, carryingChest);
    }
}
