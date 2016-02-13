package net.glowstone.entity.monster;

import com.flowpowered.networking.Message;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

import java.util.List;

public class GlowEnderman extends GlowMonster implements Enderman {

    private MaterialData carriedMaterial = new MaterialData(Material.AIR);

    public GlowEnderman(Location loc) {
        super(loc, EntityType.ENDERMAN);
        setMaxHealthAndHealth(40);
    }

    @Override
    public List<Message> createSpawnMessage() {
        metadata.set(MetadataIndex.ENDERMAN_ALERTED, isScreaming() ? (byte) 1 : (byte) 0);
        metadata.set(MetadataIndex.ENDERMAN_BLOCK, new Integer(carriedMaterial.getItemTypeId()).shortValue());
        metadata.set(MetadataIndex.ENDERMAN_BLOCK_DATA, carriedMaterial.getData());
        return super.createSpawnMessage();
    }

    @Override
    public MaterialData getCarriedMaterial() {
        return this.carriedMaterial;
    }

    @Override
    public void setCarriedMaterial(MaterialData type) {
        this.carriedMaterial = type;
        metadata.set(MetadataIndex.ENDERMAN_BLOCK, new Integer(type.getItemTypeId()).shortValue());
        metadata.set(MetadataIndex.ENDERMAN_BLOCK_DATA, type.getData());
    }

    public boolean isScreaming() {
        return metadata.getByte(MetadataIndex.ENDERMAN_ALERTED) == 1;
    }

    public void setScreaming(boolean screaming) {
        metadata.set(MetadataIndex.ENDERMAN_ALERTED, screaming ? (byte) 1 : (byte) 0);
    }
}
