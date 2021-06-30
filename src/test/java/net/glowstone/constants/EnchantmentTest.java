package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for {@link GlowEnchantment}.
 */
public class EnchantmentTest {

    private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

    @BeforeAll
    public static void staticSetup() {
        GlowEnchantment.register();
    }

    public static Collection<Field> data() {
        Field[] fields = Enchantment.class.getFields();
        List<Field> result = new ArrayList<>(fields.length);
        for (Field field : Enchantment.class.getFields()) {
            if (field.getType() == Enchantment.class
                    && (field.getModifiers() & STATIC_FINAL) == STATIC_FINAL) {
                result.add(field);
            }
        }
        return result;
    }

    @MethodSource("data")
    @ParameterizedTest
    public void effect(Field field) throws ReflectiveOperationException {
        EnchantmentWrapper wrapper = (EnchantmentWrapper) field.get(null);
        GlowEnchantment enchant = (GlowEnchantment) wrapper.getEnchantment();
        assertThat("missing enchantment for " + field.getName(), enchant, notNullValue());
        assertThat("wrong name on wrapped effect", enchant.getName(), is(field.getName()));
        assertThat("missing from byName", Enchantment.getByName(enchant.getName()), is(enchant));
        assertThat("wrong start level", enchant.getStartLevel(), is(1));
        assertThat("weird max level: " + enchant.getMaxLevel(),
            enchant.getMaxLevel() >= 1 && enchant.getMaxLevel() <= 5, is(true));
        assertThat("missing item target", enchant.getItemTarget(), notNullValue());
    }

}
