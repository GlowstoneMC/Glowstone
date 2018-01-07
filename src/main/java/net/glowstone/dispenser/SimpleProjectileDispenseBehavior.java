package net.glowstone.dispenser;

import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import net.glowstone.GlowWorld;
import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class SimpleProjectileDispenseBehavior extends ProjectileDispenseBehavior {

    private final BiFunction<? super Location, ? super ItemStack, ? extends Projectile>
            projectileCreator;

    public SimpleProjectileDispenseBehavior(Function<? super Location, ? extends Projectile>
            projectileCreator) {
        this((location, ignoredItem) -> projectileCreator.apply(location));
    }

    /**
     * Returns a {@link Location} corresponding to the given world and position vector.
     *
     * @param world the world
     * @param position the absolute coordinates
     * @return {@code position} in {@code world} as a {@link Location}
     */
    protected static Location worldAndVectorToLocation(GlowWorld world, Vector position) {
        return new Location(world, position.getX(), position.getY(), position.getZ());
    }

    @Override
    protected Projectile getProjectileEntity(GlowWorld world, Vector position,
            ItemStack item) {
        return projectileCreator.apply(worldAndVectorToLocation(world, position), item);
    }
}
