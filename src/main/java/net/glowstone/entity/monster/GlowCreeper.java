package net.glowstone.entity.monster;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

/**
 *
 * @author TheMCPEGamer
 */
public class GlowCreeper extends GlowMonster implements Creeper
{
    private boolean powered = false;
    
    public GlowCreeper(Location loc)
    {
        super(loc, EntityType.CREEPER);
    }
    
    @Override
    public boolean isPowered()
    {
        return this.powered;
    }
    
    @Override
    public void setPowered(boolean value)
    {
        this.powered = value;
    }
}
