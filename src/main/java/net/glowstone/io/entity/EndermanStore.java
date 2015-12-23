package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEnderman;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.material.MaterialData;

public class EndermanStore extends MonsterStore<GlowEnderman> {

    public EndermanStore() {
        super(GlowEnderman.class, "Enderman");
    }

    @Override
    public void load(GlowEnderman entity, CompoundTag compound) {
        super.load(entity, compound);
        MaterialData data = new MaterialData(compound.getShort("carried"), (byte) compound.getShort("carriedData"));
        entity.setCarriedMaterial(data);
    }

    @Override
    public void save(GlowEnderman entity, CompoundTag tag) {
        super.save(entity, tag);
        MaterialData data = entity.getCarriedMaterial();
        tag.putShort("carried", data.getItemType().getId());
        tag.putShort("carriedData", data.getData());
    }

}
