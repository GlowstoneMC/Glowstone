package net.glowstone.entity;

import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;

public abstract class GlowAgeableTest<T extends GlowAgeable> extends GlowLivingEntityTest<T> {

    protected GlowAgeableTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testPulse() {
        entity.setBaby();
        int prevAge = entity.getAge();
        entity.pulse();
        assertEquals(prevAge + 1, entity.getAge());
    }

    @Test
    public void testSetAgeBaby() {
        entity.setAge(-1);
        assertEquals(-1, entity.getAge());
        assertBaby(entity);
    }

    @SuppressWarnings("unchecked")
    private void assertBaby(Ageable ageable) {
        assertFalse(ageable.isAdult());
        // Check that scale is less than 1
        // FIXME: assertTrue(ageable.getWidth() < ageable.width);
        // FIXME: assertTrue(ageable.getHeight() < ageable.height);
    }

    @Test
    public void testSetAgeAdult() {
        entity.setAge(0);
        assertEquals(0, entity.getAge());
        assertAdult(entity);
        assertTrue(entity.canBreed());
    }

    @Test
    public void testSetAgeAdultCannotBreed() {
        entity.setAge(1);
        assertEquals(1, entity.getAge());
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    protected void assertAdult(T ageable) {
        assertTrue(ageable.isAdult());
        // Check that scale is at least 1
        assertTrue(ageable.getWidth() >= ageable.width);
        assertTrue(ageable.getHeight() >= ageable.height);
    }

    @Test
    public void testGetAgeLock() {
        // TODO
    }

    @Test
    public void testSetBaby() {
        entity.setBaby();
        assertBaby(entity);
    }

    @Test
    public void testSetAdult() {
        entity.setAdult();
        assertAdult(entity);
    }

    @Test
    public void testSetBreedTrueBaby() {
        entity.setBaby();
        entity.setBreed(true);
        assertAdult(entity);
        assertTrue(entity.canBreed());
    }

    @Test
    public void testSetBreedTrueAdult() {
        entity.setAge(1);
        assertFalse(entity.canBreed());
        entity.setBreed(true);
        assertAdult(entity);
        assertTrue(entity.canBreed());
    }

    @Test
    public void testSetBreedFalseBaby() {
        entity.setBaby();
        entity.setBreed(false);
        assertBaby(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    public void testSetBreedFalseAdult() {
        entity.setAdult();
        entity.setBreed(false);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    public void testSetScaleForAge() {
        // TODO
    }

    @Test
    public void testSetScale() {
        // TODO
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateBaby() {
        Mockito.when(world.spawn(any(Location.class),
                (Class<? extends GlowEntity>) any(Class.class),
                eq(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)))
                .thenCallRealMethod();
        T baby = (T) entity.createBaby();
        assertNotNull(baby);
        assertNotEquals(entity, baby);
        assertEquals(entity.getClass(), baby.getClass());
        assertEquals(entity, baby.getParent());
    }

    @Test
    public void testGetSoundPitch() {
        // TODO
    }

    @Test
    public void testComputeGrowthAmount() {
        entity.setBaby();
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
        assertEquals(0, entity.computeGrowthAmount(Material.CARROT_ITEM));
    }

    @Test
    public void testComputeGrowthAmountAdult() {
        entity.setAge(0);
        assertEquals(0, entity.computeGrowthAmount(null));
        assertEquals(0, entity.computeGrowthAmount(Material.WHEAT));
        assertEquals(0, entity.computeGrowthAmount(Material.HAY_BLOCK));
        assertEquals(0, entity.computeGrowthAmount(Material.CARROT_ITEM));
    }

    @Test
    public void testEntityInteractGrowsBaby() {
        entity.setBaby();
        T mockedEntity = spy(entity);
        inventory.setItemInMainHand(new ItemStack(Material.RAW_FISH, 60));
        InteractEntityMessage message = new InteractEntityMessage(0, InteractEntityMessage.Action.INTERACT.ordinal(), 0);

        Mockito.when(mockedEntity.computeGrowthAmount(any())).thenReturn(100);

        mockedEntity.entityInteract(player, message);

        assertEquals(-23900, mockedEntity.getAge());
        assertEquals(59, inventory.getItemInMainHand().getAmount());
    }

    @Test
    public void testEntityInteractDoesNotGrowBaby() {
        entity.setBaby();
        inventory.setItemInMainHand(new ItemStack(Material.BEDROCK, 60));
        InteractEntityMessage message = new InteractEntityMessage(0, InteractEntityMessage.Action.INTERACT.ordinal(), 0);

        entity.entityInteract(player, message);

        assertEquals(-24000, entity.getAge());
        assertEquals(60, inventory.getItemInMainHand().getAmount());
    }
}
