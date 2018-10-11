package net.glowstone.entity.monster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.UUID;
import org.bukkit.entity.Entity;
import org.junit.Test;

public class GlowElderGuardianTest extends GlowGuardianTest {
    @Override
    @Test
    public void testIsElder() {
        assertTrue(entity.isElder());
    }

    @Override
    @Test
    public void testSetElder() {
        UUID uuid = entity.getUniqueId();
        entity.setElder(true);
        assertSame(entity, entity.getWorld().getEntity(uuid));
        entity.setElder(false);
        final Entity younger = this.entity.getWorld().getEntity(uuid);
        assertNotEquals(this.entity, younger);
        assertTrue(younger instanceof GlowGuardian);
        assertFalse(younger instanceof GlowElderGuardian);
        assertFalse(((GlowGuardian) younger).isElder());
    }

    public GlowElderGuardianTest() {
        super(GlowElderGuardian::new);
    }
}
