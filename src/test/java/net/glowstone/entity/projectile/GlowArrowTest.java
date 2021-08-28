package net.glowstone.entity.projectile;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowArrowTest extends GlowProjectileTest<GlowArrow> {
    protected GlowArrowTest(
            Function<? super Location, ? extends GlowArrow> entityCreator) {
        super(entityCreator);
    }

    public GlowArrowTest() {
        this(GlowArrow::new);
    }
}
