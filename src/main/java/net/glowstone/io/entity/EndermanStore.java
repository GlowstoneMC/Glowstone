package net.glowstone.io.entity;

import java.util.Optional;
import net.glowstone.constants.ItemIds;
import net.glowstone.entity.monster.GlowEnderman;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

class EndermanStore extends MonsterStore<GlowEnderman> {

    public EndermanStore() {
        super(GlowEnderman.class, EntityType.ENDERMAN, GlowEnderman::new);
    }

    @Override
    public void load(GlowEnderman entity, CompoundTag compound) {
        super.load(entity, compound);
        // Load carried block. May be saved as a String or short ID.
        // If the id is 0 or AIR, it is ignored.
        compound.tryGetMaterial("carried")
                .map(MaterialData::new)
                .ifPresent(carried -> {
                    compound.readShort("carriedData", data -> carried.setData((byte) data));
                    entity.setCarriedMaterial(carried);
                });
    }

    @Override
    public void save(GlowEnderman entity, CompoundTag tag) {
        super.save(entity, tag);
        MaterialData carried = entity.getCarriedMaterial();
        // Save the carried block, if there is one.
        if (carried != null && carried.getItemType() != Material.AIR) {
            tag.putShort("carried", carried.getItemType().getId());
            tag.putShort("carriedData", carried.getData());
        }
    }
}
