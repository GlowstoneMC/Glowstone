package net.glowstone.entity.passive;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

//TODO: Further implementation
public class GlowWolf extends GlowTameable implements Wolf {

    private boolean angry;
    private boolean sitting;
    private DyeColor collarColor;

    public GlowWolf(Location location) {
        super(location, EntityType.WOLF);
    }

    @Override
    public boolean isAngry() {
        return angry;
    }

    @Override
    public void setAngry(boolean angry) {
        this.angry = angry;
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
    public DyeColor getCollarColor() {
        return collarColor;
    }

    @Override
    public void setCollarColor(DyeColor color) {
        this.collarColor = color;
    }
}
