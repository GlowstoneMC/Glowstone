package net.glowstone.entity.monster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.UUID;
import java.util.function.Function;

import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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
        UUID uuid = new UUID(100, 200);
        GlowElderGuardian other = new GlowElderGuardian(entity.getLocation());
        Mockito.when(world.spawn(eq(entity.getLocation()), eq(GlowElderGuardian.class))).thenReturn(other);
        Mockito.when(world.getUID()).thenReturn(uuid);
        Mockito.when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        Mockito.when(server.getWorld(uuid)).thenReturn(world);

        entity.setFireTicks(23);
        Assert.assertEquals(23, entity.getFireTicks());
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", 23);
        Assert.assertEquals(23, tag.getShort("Fire"));
        EntityStorage.save(entity, tag);

        entity.setElder(true);
        Assert.assertEquals(23, other.getFireTicks());
    }


}
