package net.glowstone.mixin;

import net.glowstone.interfaces.ILocation;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = org.bukkit.Location.class, remap = false)
public class MixinBukkitLocation implements ILocation {

    @Shadow
    private org.bukkit.World world;
    @Shadow
    private double x;
    @Shadow
    private double y;
    @Shadow
    private double z;

    @Override
    public org.bukkit.Location toBukkit() {
        return (org.bukkit.Location) (Object) this;
    }

    @Override
    public Location<World> toSponge() {
        return new Location<>((World) world, x, y, z);
    }
}
