package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;

/**
 *
 * @author TheMCPEGamer
 */
public class GlowEnderman extends GlowMonster implements Enderman
{
    private MaterialData carriedMaterial = new MaterialData(Material.AIR);
    
    public GlowEnderman(Location loc)
    {
        super(loc, EntityType.ENDERMAN);
    }
    
    @Override
    public MaterialData getCarriedMaterial()
    {
        return this.carriedMaterial;
    }
    
    @Override
    public void setCarriedMaterial(MaterialData type)
    {
        this.carriedMaterial = type;
    }
}
