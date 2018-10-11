package net.glowstone.entity.monster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.junit.Test;

public class GlowGuardianTest extends GlowMonsterTest<GlowGuardian> {

    public GlowGuardianTest() {
        this(GlowGuardian::new);
    }

    protected GlowGuardianTest(
            Function<Location, ? extends GlowGuardian> entityCreator) {
        super(entityCreator);
    }

    @Test
    public void testIsElder() {
        assertFalse(entity.isElder());
    }

    @Test
    public void testSetElder() {
        UUID uuid = entity.getUniqueId();
        entity.setElder(false);
        assertSame(entity, entity.getWorld().getEntity(uuid));
        entity.setElder(true);
        final Entity elder = this.entity.getWorld().getEntity(uuid);
        assertNotEquals(this.entity, elder);
        assertTrue(elder instanceof GlowElderGuardian);
        assertTrue(((GlowGuardian) elder).isElder());
    }
}
