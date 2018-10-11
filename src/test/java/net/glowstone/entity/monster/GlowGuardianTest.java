package net.glowstone.entity.monster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.function.Function;
import net.glowstone.entity.GlowEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
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
        when(world.getEntities()).thenCallRealMethod();
        when(world.getEntity(any(UUID.class))).thenCallRealMethod();
        when(world.spawn(any(Location.class), (Class<? extends Entity>) any(Class.class)))
                .thenCallRealMethod();
        when(world.spawn(any(Location.class), (Class<? extends GlowEntity>) any(Class.class),
                any(CreatureSpawnEvent.SpawnReason.class)))
                .thenCallRealMethod();
        // Must use world.spawn so that entity will be retrievable by uuid
        //entity = world.spawn(location, GlowGuardian.class);
        entityManager.forEach(knownEntity -> System.out.format("%s {%s}\n", knownEntity, knownEntity.getUniqueId()));
        UUID uuid = entity.getUniqueId();
        System.out.println(uuid);
        entity.setElder(false);
        assertSame(entity, world.getEntity(uuid));
        entity.setElder(true);
        final Entity elder = world.getEntity(uuid);
        assertNotEquals(this.entity, elder);
        assertTrue(elder instanceof GlowElderGuardian);
        assertTrue(((GlowGuardian) elder).isElder());
        assertEquals(uuid, elder.getUniqueId());
        assertEquals(location, elder.getLocation());
    }
}
