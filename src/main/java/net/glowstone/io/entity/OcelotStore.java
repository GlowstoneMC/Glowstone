package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowOcelot;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;

class OcelotStore extends TameableStore<GlowOcelot> {

    public OcelotStore() {
        super(GlowOcelot.class, "Ozelot");
    }

    @Override
    public void load(GlowOcelot entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isInt("CatType")) {
            entity.setCatType(Ocelot.Type.getType(compound.getInt("CatType")));
        } else {
            entity.setCatType(Type.WILD_OCELOT);
        }

    }

    @Override
    public void save(GlowOcelot entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("CatType", entity.getCatType().getId());
    }

}
