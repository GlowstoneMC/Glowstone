package net.glowstone.entity.monster.complex;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.Collections;
import net.glowstone.entity.GlowEntityTest;

public class GlowEnderDragonPartTest extends GlowEntityTest<GlowEnderDragonPart> {
    public GlowEnderDragonPartTest() {
        super(location -> new GlowEnderDragonPart(new GlowEnderDragon(location)));
    }

    @Override
    public void testCreateSpawnMessage() {
        // An ender-dragon part has no spawn messages, because the client derives it from the parent
        // entity's spawn message.
        assertIterableEquals(entity.createSpawnMessage(), Collections.emptyList());
    }
}
