package net.glowstone.entity;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowHumanEntityTest<T extends GlowHumanEntity> extends GlowLivingEntityTest<T> {
    protected GlowHumanEntityTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }
}
