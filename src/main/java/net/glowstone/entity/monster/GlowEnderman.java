package net.glowstone.entity.monster;

import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

public class GlowEnderman extends GlowMonster implements Enderman {

    private MaterialData carriedMaterial = new MaterialData(Material.AIR);

    public GlowEnderman(Location loc) {
        super(loc, EntityType.ENDERMAN, 40);
    }

    @Override
    public MaterialData getCarriedMaterial() {
        return this.carriedMaterial;
    }

    @Override
    public void setCarriedMaterial(MaterialData type) {
        this.carriedMaterial = type;
        metadata.set(MetadataIndex.ENDERMAN_BLOCK, Integer.valueOf(type.getItemTypeId()).shortValue());
        metadata.set(MetadataIndex.ENDERMAN_BLOCK_DATA, type.getData());
    }

    public boolean isScreaming() {
        return metadata.getByte(MetadataIndex.ENDERMAN_ALERTED) == 1;
    }

    public void setScreaming(boolean screaming) {
        metadata.set(MetadataIndex.ENDERMAN_ALERTED, screaming ? (byte) 1 : (byte) 0);
    }
}
