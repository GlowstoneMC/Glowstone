package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowSheep;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class SheepStore extends AgeableStore<GlowSheep> {

    public static final String SHEARED_KEY = "Sheared";
    public static final String COLOR_KEY = "Color";

    public SheepStore() {
        super(GlowSheep.class, EntityType.SHEEP, GlowSheep::new);
    }

    @Override
    public GlowSheep createEntity(Location location, CompoundTag compound) {
        return new GlowSheep(location);
    }

    @Override
    public void load(GlowSheep entity, CompoundTag compound) {
        super.load(entity, compound);
        if (!compound.readByte(COLOR_KEY, color -> entity.setColor(DyeColor.values()[color]))) {
            entity.setColor(DyeColor.WHITE);
        }
        entity.setSheared(compound.getBoolean(SHEARED_KEY, false));
    }

    @Override
    public void save(GlowSheep entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte(COLOR_KEY, entity.getColor().ordinal());
        tag.putBool(SHEARED_KEY, entity.isSheared());
    }
}
