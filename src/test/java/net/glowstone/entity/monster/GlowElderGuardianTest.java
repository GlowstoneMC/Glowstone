package net.glowstone.entity.monster;

import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.World;
import org.junit.Assert;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

public class GlowElderGuardianTest extends GlowGuardianTest {
    @Override
    public void testIsElder() {
        assertTrue(entity.isElder());
    }

    @Override
    public void testSetElder() {
        UUID uuid = new UUID(100, 200);
        GlowGuardian other = new GlowGuardian(entity.getLocation());
        Mockito.when(world.spawn(eq(entity.getLocation()), eq(GlowGuardian.class))).thenReturn(other);
        Mockito.when(world.getUID()).thenReturn(uuid);
        Mockito.when(world.getEnvironment()).thenReturn(World.Environment.NORMAL);
        Mockito.when(server.getWorld(uuid)).thenReturn(world);

        entity.setFireTicks(23);
        Assert.assertEquals(23, entity.getFireTicks());
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", 23);
        Assert.assertEquals(23, tag.getShort("Fire"));
        EntityStorage.save(entity, tag);

        entity.setElder(false);
        Assert.assertEquals(23, other.getFireTicks());
    }

    public GlowElderGuardianTest() {
        super(GlowElderGuardian::new);
    }
}
