package net.glowstone.entity.monster;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowSkeletonTest extends GlowMonsterTest<GlowSkeleton> {

    protected GlowSkeletonTest(
            Function<Location, ? extends GlowSkeleton> entityCreator) {
        super(entityCreator);
    }

    public GlowSkeletonTest() {
        this(GlowSkeleton::new);
    }
}
