package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimal;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PolarBear;

public class GlowPolarBear extends GlowAnimal implements PolarBear {

    public GlowPolarBear(Location location) {
        super(location, EntityType.POLAR_BEAR, 30);
    }

    @Override
    public boolean isStanding() {
        return metadata.getBoolean(MetadataIndex.POLARBEAR_STANDING);
    }

    @Override
    public void setStanding(boolean standing) {
        metadata.set(MetadataIndex.POLARBEAR_STANDING, standing);
    }
}
