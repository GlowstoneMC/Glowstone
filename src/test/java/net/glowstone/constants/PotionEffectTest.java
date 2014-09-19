package net.glowstone.constants;

import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowPotionEffect}.
 */
@RunWith(Parameterized.class)
public class PotionEffectTest {

    private final static int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

    private final Field field;

    public PotionEffectTest(Field field) {
        this.field = field;
    }

    @BeforeClass
    public static void staticSetup() {
        GlowPotionEffect.register();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCases() {
        Field[] fields = PotionEffectType.class.getFields();
        List<Object[]> result = new ArrayList<>(fields.length);
        for (Field field : PotionEffectType.class.getFields()) {
            if (field.getType() == PotionEffectType.class && (field.getModifiers() & STATIC_FINAL) == STATIC_FINAL) {
                result.add(new Object[]{field});
            }
        }
        return result;
    }

    @Test
    public void effect() throws ReflectiveOperationException {
        PotionEffectTypeWrapper wrapper = (PotionEffectTypeWrapper) field.get(null);
        GlowPotionEffect effect = (GlowPotionEffect) wrapper.getType();
        assertNotNull("missing potion effect for " + field.getName(), effect);
        assertEquals("wrong name on wrapped effect", field.getName(), effect.getName());
        assertEquals("missing from byName", effect, PotionEffectType.getByName(effect.getName()));
        assertEquals("missing from byId", effect, PotionEffectType.getById(effect.getId()));
        assertTrue("non-positive duration amplifier for " + effect, effect.getDurationModifier() > 0);
    }

}
