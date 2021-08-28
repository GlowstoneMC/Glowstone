package net.glowstone.entity.projectile;

import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Location;

import java.util.function.Function;

public abstract class GlowProjectileTest<T extends GlowProjectile> extends GlowEntityTest<T> {

    protected GlowProjectileTest(
            Function<? super Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
