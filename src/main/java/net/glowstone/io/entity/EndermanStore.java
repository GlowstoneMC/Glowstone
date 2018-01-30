package net.glowstone.io.entity;

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
        MaterialData carried = null;
        // Load carried block. May be saved as a String or short ID.
        // If the id is 0 or AIR, it is ignored.
        if (compound.isShort("carried")) {
            short id = compound.getShort("carried");
            Material type = Material.getMaterial(id);
            if (type != null && type != Material.AIR) {
                carried = new MaterialData(type);
            }
        } else if (compound.isString("carried")) {
            String id = compound.getString("carried");
            if (!id.isEmpty()) {
                if (!id.contains(":")) {
                    // There is no namespace, so prepend the default minecraft: namespace
                    id = "minecraft:" + id;
                }
                Material type = ItemIds.getBlock(id);
                if (type == null) {
                    // Not a block, might be an item
                    type = ItemIds.getItem(id);
                }
                if (type != null && type != Material.AIR) {
                    carried = new MaterialData(type);
                }
            }
        }
        if (carried != null) {
            if (compound.isShort("carriedData")) {
                carried.setData((byte) compound.getShort("carriedData"));
            }
            entity.setCarriedMaterial(carried);
        }
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
