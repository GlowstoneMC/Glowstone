package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.hamcrest.number.OrderingComparison;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowPotionEffect}.
 */
public class PotionEffectTest {

    @BeforeClass
    public static void staticSetup() {
        GlowPotionEffect.register();
    }

    @DataProvider(name = "fields")
    public static Iterator<Object[]> data() {
        return TestUtils.staticFinalFieldsDataProvider(
                PotionEffectType.class, PotionEffectType.class);
    }

    @Test(dataProvider = "fields")
    public void effect(Field field) throws ReflectiveOperationException {
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
