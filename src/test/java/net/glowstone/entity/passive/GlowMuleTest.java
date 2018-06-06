package net.glowstone.entity.passive;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.Test;

public class GlowMuleTest extends GlowChestedHorseTest<GlowMule> {
    public GlowMuleTest() {
        super(GlowMule::new);
    }

    @Test
    @Override
    public void testSetAgeAdult() {
        entity.setAge(0);
        assertEquals(0, entity.getAge());
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueBaby() {
        entity.setBaby();
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

    @Test
    @Override
    public void testSetBreedTrueAdult() {
        entity.setAge(1);
        assertFalse(entity.canBreed());
        entity.setBreed(true);
        assertAdult(entity);
        assertFalse(entity.canBreed());
    }

}
