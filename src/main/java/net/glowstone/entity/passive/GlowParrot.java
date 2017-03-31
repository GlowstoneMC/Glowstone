package net.glowstone.entity.passive;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Parrot;

import java.util.concurrent.ThreadLocalRandom;

public class GlowParrot extends GlowTameable implements Parrot {

    public static final Variant[] VARIANTS = Variant.values();
    private boolean sitting;

    public GlowParrot(Location location) {
        super(location, EntityType.PARROT, 6);
        setBoundingBox(0.5, 1.0);
        setSitting(false);
        setVariant(VARIANTS[ThreadLocalRandom.current().nextInt(VARIANTS.length)]);
    }

    @Override
    public boolean isSitting() {
        return sitting;
    }

    @Override
    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    @Override
    public Variant getVariant() {
        int variantId = metadata.getInt(MetadataIndex.PARROT_COLOR);
        return (variantId >= VARIANTS.length || variantId < 0) ? VARIANTS[0] : VARIANTS[variantId];
    }

    @Override
    public void setVariant(Variant variant) {
        metadata.set(MetadataIndex.PARROT_COLOR, variant.ordinal());
    }
}
