package net.glowstone.mixin;

import net.glowstone.interfaces.ILocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Location.class)
public class MixinSpongeLocation implements ILocation {

    @Override
    public org.bukkit.Location toBukkit() {
        Location<World> this0 = toSponge();
        return new org.bukkit.Location((org.bukkit.World) this0.getExtent(), this0.getX(), this0.getY(), this0.getZ());
    }

    @Override
    public Location<World> toSponge() {
        return (Location) (Object) this;
    }
}
