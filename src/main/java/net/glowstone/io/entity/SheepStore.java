package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowSheep;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.DyeColor;
import org.bukkit.Location;

class SheepStore extends AgeableStore<GlowSheep> {

    public static final String SHEARED_KEY = "Sheared";
    public static final String COLOR_KEY = "Color";

    public SheepStore() {
        super(GlowSheep.class, "Sheep");
    }

    @Override
    public GlowSheep createEntity(Location location, CompoundTag compound) {
        return new GlowSheep(location);
    }

    public void load(GlowSheep entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setColor(DyeColor.values()[compound.getByte(COLOR_KEY)]);
        entity.setSheared(compound.getBool(SHEARED_KEY));
    }

    public void save(GlowSheep entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte(COLOR_KEY, entity.getColor().ordinal());
        tag.putBool(SHEARED_KEY, entity.isSheared());
    }
}
