package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public abstract class GlowLivingEntityTest<T extends GlowLivingEntity> extends GlowEntityTest<T> {
    protected GlowLivingEntityTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
