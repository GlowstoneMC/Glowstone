package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.testng.AssertJUnit.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowEnchantment}.
 */
public class EnchantmentTest {

    private static final int STATIC_FINAL = Modifier.STATIC | Modifier.FINAL;

    @BeforeClass
    public static void staticSetup() {
        GlowEnchantment.register();
    }

    @DataProvider(name = "fields")
    public static Iterator<Object[]> data() {
        return TestUtils.staticFinalFieldsDataProvider(Enchantment.class, Enchantment.class);
    }

    @Test(dataProvider = "fields")
    public void effect(Field field) throws ReflectiveOperationException {
        EnchantmentWrapper wrapper = (EnchantmentWrapper) field.get(null);
        GlowEnchantment enchant = (GlowEnchantment) wrapper.getEnchantment();
        assertThat("missing enchantment for " + field.getName(), enchant, notNullValue());
        assertThat("wrong name on wrapped effect", enchant.getName(), is(field.getName()));
        assertThat("missing from byName", Enchantment.getByName(enchant.getName()), is(enchant));
        assertThat("missing from byId", Enchantment.getById(enchant.getId()), is(enchant));
        assertThat("wrong start level", enchant.getStartLevel(), is(1));
        assertThat("weird max level: " + enchant.getMaxLevel(),
            enchant.getMaxLevel() >= 1 && enchant.getMaxLevel() <= 5, is(true));
        assertThat("missing item target", enchant.getItemTarget(), notNullValue());
    }

}
