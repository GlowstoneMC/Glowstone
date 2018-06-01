package net.glowstone.entity.monster;

import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

public class GlowEnderman extends GlowMonster implements Enderman {

    @Getter
    private MaterialData carriedMaterial = new MaterialData(Material.AIR);

    public GlowEnderman(Location loc) {
        super(loc, EntityType.ENDERMAN, 40);
        setBoundingBox(0.6, 2.9);
    }

    @Override
    public boolean teleportRandomly() {
        return false; // TODO: teleport Enderman
    }

    @Override
    public void setCarriedMaterial(MaterialData type) {
        carriedMaterial = type;
        if (type == null) {
            metadata.set(MetadataIndex.ENDERMAN_BLOCK, 0);
        } else {
            // TODO: store block data. This code appears to be broken (although documented in the
            // protocol): int blockId = type.getItemTypeId() << 4 | type.getData();
            metadata.set(MetadataIndex.ENDERMAN_BLOCK, type.getItemTypeId());
        }
    }

    public boolean isScreaming() {
        return metadata.getBoolean(MetadataIndex.ENDERMAN_SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        metadata.set(MetadataIndex.ENDERMAN_SCREAMING, screaming);
    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ENDERMEN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERMEN_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ENDERMEN_AMBIENT;
    }
}
