package net.glowstone.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.function.Function;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.junit.Before;
import org.junit.Test;

public abstract class GlowAgeableTest<T extends GlowAgeable> extends GlowLivingEntityTest<T> {

    protected GlowAgeableTest(
            Function<Location, ? extends T> entityCreator) {
        super(entityCreator);
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
    }

    @Test
    public void testPulse() {
        T ageable = entityCreator.apply(location);
        ageable.setBaby();
        int prevAge = ageable.getAge();
        ageable.pulse();
        assertEquals(prevAge + 1, ageable.getAge());
    }

    @Test
    public void testSetAgeBaby() {
        T ageable = entityCreator.apply(location);
        ageable.setAge(-1);
        assertEquals(-1, ageable.getAge());
        assertBaby(ageable);
    }

    private void assertBaby(Ageable ageable) {
        assertFalse(ageable.isAdult());
        // Check that scale is less than 1
        // FIXME: assertTrue(ageable.getWidth() < ageable.width);
        // FIXME: assertTrue(ageable.getHeight() < ageable.height);
    }

    @Test
    public void testSetAgeAdult() {
        T ageable = entityCreator.apply(location);
        ageable.setAge(0);
        assertEquals(0, ageable.getAge());
        assertAdult(ageable);
    }

    private void assertAdult(T ageable) {
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
        T ageable = entityCreator.apply(location);
        ageable.setBaby();
        assertBaby(ageable);
    }

    @Test
    public void testSetAdult() {
        T ageable = entityCreator.apply(location);
        ageable.setAdult();
        assertAdult(ageable);
    }

    @Test
    public void testSetBreedTrueBaby() {
        T ageable = entityCreator.apply(location);
        ageable.setBaby();
        ageable.setBreed(true);
        assertAdult(ageable);
        assertTrue(ageable.canBreed());
    }

    @Test
    public void testSetBreedTrueAdult() {
        T ageable = entityCreator.apply(location);
        ageable.setAge(1);
        assertFalse(ageable.canBreed());
        ageable.setBreed(true);
        assertAdult(ageable);
        assertTrue(ageable.canBreed());
    }

    @Test
    public void testSetBreedFalseBaby() {
        T ageable = entityCreator.apply(location);
        ageable.setBaby();
        ageable.setBreed(false);
        assertBaby(ageable);
        assertFalse(ageable.canBreed());
    }

    @Test
    public void testSetBreedFalseAdult() {
        T ageable = entityCreator.apply(location);
        ageable.setAdult();
        ageable.setBreed(false);
        assertAdult(ageable);
        assertFalse(ageable.canBreed());
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
        T ageable = entityCreator.apply(location);
        T baby = (T) ageable.createBaby();
        assertNotEquals(ageable, baby);
        assertEquals(ageable.getClass(), baby.getClass());
        assertEquals(ageable, baby.getParent());
    }

    @Test
    public void testGetSoundPitch() {
        // TODO
    }
}