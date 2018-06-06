package net.glowstone.entity.objects;

import java.util.function.Function;
import java.util.stream.Stream;
import net.glowstone.entity.GlowEntityTest;
import org.testng.annotations.Factory;

public class GlowMinecartTest extends GlowEntityTest<GlowMinecart> {

    @Factory
    public static Object[] instances() {
        return Stream.of(GlowMinecart.MinecartType.values())
                .map((Function<GlowMinecart.MinecartType, Object>) GlowMinecartTest::new)
                .toArray();
    }

    public GlowMinecartTest(GlowMinecart.MinecartType type) {
        super(type.getCreator());
    }
}
