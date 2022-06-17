package net.glowstone.entity.monster;

import lombok.Getter;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.util.MaterialUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

public class GlowEnderman extends GlowMonster implements Enderman {

    // TODO: 1.13 block data
    @Getter
    private BlockData carriedBlock = Bukkit.getServer().createBlockData(Material.AIR);

    public GlowEnderman(Location loc) {
        super(loc, EntityType.ENDERMAN, 40);
        setBoundingBox(0.6, 2.9);
    }

    @Override
    public boolean teleportRandomly() {
        return false; // TODO: teleport Enderman
    }

    @Override
    public @NotNull MaterialData getCarriedMaterial() {
        return null;
    }

    @Override
    public void setCarriedMaterial(@NotNull MaterialData material) {

    }

    public void setCarriedBlock(BlockData type) {
        carriedBlock = type;
        if (type == null) {
            metadata.set(MetadataIndex.ENDERMAN_BLOCK, 0);
        } else {
            // TODO: store block data. This code appears to be broken (although documented in the
            // protocol): int blockId = type.getItemTypeId() << 4 | type.getData();
            metadata.set(MetadataIndex.ENDERMAN_BLOCK, MaterialUtil.getId(type));
        }
    }

    public boolean isScreaming() {
        return metadata.getBoolean(MetadataIndex.ENDERMAN_SCREAMING);
    }

    public void setScreaming(boolean screaming) {
        metadata.set(MetadataIndex.ENDERMAN_SCREAMING, screaming);
    }

    @Override
    public boolean hasBeenStaredAt() {
        return false;
    }

    @Override
    public void setHasBeenStaredAt(boolean hasBeenStaredAt) {

    }

    @Override
    protected Sound getHurtSound() {
        return Sound.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected Sound getDeathSound() {
        return Sound.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    protected Sound getAmbientSound() {
        return Sound.ENTITY_ENDERMAN_AMBIENT;
    }
}
