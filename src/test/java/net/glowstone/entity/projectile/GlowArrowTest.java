package net.glowstone.entity.projectile;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowArrowTest extends GlowProjectileTest<GlowArrow> {
    protected GlowArrowTest(
            Function<? super Location, ? extends GlowArrow> entityCreator) {
        super(entityCreator);
    }

    public GlowArrowTest() {
        this(GlowArrow::new);
    }
}
