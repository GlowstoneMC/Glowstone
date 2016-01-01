package net.glowstone.entity.passive;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;

public class GlowOcelot extends GlowTameable implements Ocelot {

    private Type catType;
    private boolean sitting;

    public GlowOcelot(Location location) {
        super(location, EntityType.OCELOT);
    }

    @Override
    public Type getCatType() {
        return catType;
    }

    @Override
    public void setCatType(Type type) {
        this.catType = type;
    }

    @Override
    public boolean isSitting() {
        return sitting;
    }

    @Override
    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }
}
