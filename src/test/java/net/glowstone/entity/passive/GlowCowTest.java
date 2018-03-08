package net.glowstone.entity.passive;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;
import net.glowstone.entity.GlowAgeableTest;
import org.bukkit.Location;
import org.junit.jupiter.api.Test;

public class GlowCowTest extends GlowAgeableTest<GlowCow> {

    protected GlowCowTest() {
        super(GlowCow::new);
    }

    @Test
    public void testEntityInteract() {
        // TODO
    }
}