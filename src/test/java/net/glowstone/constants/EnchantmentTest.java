package net.glowstone.constants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GlowEnchantment}.
 */
@RunWith(Parameterized.class)
public class EnchantmentTest {

    private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

    private final Field field;

    public EnchantmentTest(Field field) {
        this.field = field;
    }

    @BeforeClass
    public static void staticSetup() {
        GlowEnchantment.register();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Field[] fields = Enchantment.class.getFields();
        List<Object[]> result = new ArrayList<>(fields.length);
        for (Field field : Enchantment.class.getFields()) {
            if (field.getType() == Enchantment.class && (field.getModifiers() & STATIC_FINAL) == STATIC_FINAL) {
                result.add(new Object[]{field});
            }
        }
        return result;
    }

    @Test
    public void effect() throws ReflectiveOperationException {
        EnchantmentWrapper wrapper = (EnchantmentWrapper) field.get(null);
        GlowEnchantment enchant = (GlowEnchantment) wrapper.getEnchantment();
        assertThat("missing enchantment for " + field.getName(), enchant, notNullValue());
        assertThat("wrong name on wrapped effect", enchant.getName(), is(field.getName()));
        assertThat("missing from byName", Enchantment.getByName(enchant.getName()), is(enchant));
        assertThat("missing from byId", Enchantment.getById(enchant.getId()), is(enchant));
        assertThat("wrong start level", enchant.getStartLevel(), is(1));
        assertThat("weird max level: " + enchant.getMaxLevel(), enchant.getMaxLevel() >= 1 && enchant.getMaxLevel() <= 5, is(true));
        assertThat("missing item target", enchant.getItemTarget(), notNullValue());
    }

}
