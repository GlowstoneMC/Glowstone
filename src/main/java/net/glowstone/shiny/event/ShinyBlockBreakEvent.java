package net.glowstone.shiny.event;

import org.spongepowered.api.event.block.BlockBreakEvent;
import org.spongepowered.api.world.Location;

public class ShinyBlockBreakEvent extends ShinyBlockEvent implements BlockBreakEvent {
    public ShinyBlockBreakEvent(Location block) {
        super(block);
    }

    public ShinyBlockBreakEvent(org.bukkit.Location location) {
        super(location);
    }
}
