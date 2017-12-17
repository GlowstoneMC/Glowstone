package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.hamcrest.number.OrderingComparison;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Tests for {@link GlowPotionEffect}.
 */
@RunWith(Parameterized.class)
public class PotionEffectTest {

    private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

    private final Field field;

    public PotionEffectTest(Field field) {
        this.field = field;
    }

    @BeforeClass
    public static void staticSetup() {
        GlowPotionEffect.register();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Field[] fields = PotionEffectType.class.getFields();
        List<Object[]> result = new ArrayList<>(fields.length);
        for (Field field : PotionEffectType.class.getFields()) {
            if (field.getType() == PotionEffectType.class
                && (field.getModifiers() & STATIC_FINAL) == STATIC_FINAL) {
                result.add(new Object[]{field});
            }
        }
        return result;
    }

    @Test
    public void effect() throws ReflectiveOperationException {
        PotionEffectTypeWrapper wrapper = (PotionEffectTypeWrapper) field.get(null);
        GlowPotionEffect effect = (GlowPotionEffect) wrapper.getType();
        assertThat("missing potion effect for " + field.getName(), effect, notNullValue());
        assertThat("wrong name on wrapped effect", effect.getName(), is(field.getName()));
        assertThat("missing from byName", PotionEffectType.getByName(effect.getName()), is(effect));
        assertThat("missing from byId", PotionEffectType.getById(effect.getId()), is(effect));
        assertThat("non-positive duration amplifier for " + effect, effect.getDurationModifier(),
            OrderingComparison.greaterThan(0d));
    }

}
