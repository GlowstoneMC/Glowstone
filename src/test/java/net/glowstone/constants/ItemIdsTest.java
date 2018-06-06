package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.AssertJUnit.fail;

import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.Material;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link ItemIds}.
 */
public class ItemIdsTest {

    @DataProvider
    public Iterator<Object[]> materials() {
        return TestUtils.enumAsDataProvider(Material.class);
    }

    @Test(dataProvider = "Material")
    public void mappingExists(Material material) {
        String nameid = ItemIds.getName(material);
        assertThat("Identifier missing for " + material, nameid, notNullValue());
        if (!nameid.startsWith("minecraft:")) {
            fail("Identifier '" + nameid + "' does not start with 'minecraft:'");
        }

        Material item = ItemIds.getItem(nameid);
        Material block = ItemIds.getBlock(nameid);
        String base = "Material " + material + "\t-> \"" + nameid + "\"\t-> ";
        if (material.isBlock()) {
            assertThat(base + "block, has no block entry", block, notNullValue());
            assertThat("wrong block material", block, is(material));
            if (item != material) {
                System.out.println(base + "item: " + item);
            }
        } else {
            assertThat(base + "item, has no item entry", item, notNullValue());
            assertThat("wrong item material", item, is(material));
            if (block == material) {
                fail(base + "not block, but maps to block: " + block);
            }
        }
    }

}
