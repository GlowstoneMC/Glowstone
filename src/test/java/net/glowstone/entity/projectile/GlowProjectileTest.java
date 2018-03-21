package net.glowstone.entity.projectile;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;
import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Location;

public abstract class GlowProjectileTest<T extends GlowProjectile> extends GlowEntityTest<T> {

    protected GlowProjectileTest(
            Function<? super Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
