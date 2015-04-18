package net.glowstone.shiny.event;

import net.glowstone.shiny.world.ShinyWorld;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.event.ExperienceEvent;
import org.spongepowered.api.event.block.BlockChangeEvent;
import org.spongepowered.api.event.block.BlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

public class ShinyBlockEvent extends ShinyGameEvent implements BlockEvent, BlockChangeEvent, ExperienceEvent {

    private Location block;
    private int exp;

    public ShinyBlockEvent(Location block) {
        this.block = block;
    }

    public ShinyBlockEvent(org.bukkit.Location location) {
        Extent extent = new ShinyWorld(location.getWorld());
        this.block = new Location(extent, location.getX(), location.getY(), location.getZ());
    }

    public Location getBlock() {
        return this.block;
    }

    @Override
    public BlockSnapshot getReplacementBlock() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getExp() {
        return this.exp;
    }

    @Override
    public void setExp(int exp) {
        this.exp = exp;
    }
}
