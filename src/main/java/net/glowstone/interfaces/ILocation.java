package net.glowstone.interfaces;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface ILocation {

    org.bukkit.Location toBukkit();

    Location<World> toSponge();

}
