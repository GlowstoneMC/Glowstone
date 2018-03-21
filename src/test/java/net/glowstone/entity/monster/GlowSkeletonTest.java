package net.glowstone.entity.monster;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowSkeletonTest extends GlowMonsterTest<GlowSkeleton> {

    protected GlowSkeletonTest(
            Function<Location, ? extends GlowSkeleton> entityCreator) {
        super(entityCreator);
    }

    public GlowSkeletonTest() {
        super(GlowSkeleton::new);
    }
}
