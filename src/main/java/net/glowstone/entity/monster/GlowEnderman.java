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
        metadata.set(MetadataIndex.ENDERMAN_BLOCK, new Integer(type.getItemTypeId()).shortValue());
    }

    public boolean isScreaming() {
        return metadata.getBoolean(MetadataIndex.ENDERMAN_SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        metadata.set(MetadataIndex.ENDERMAN_SCREAMING, screaming);
    }
}
