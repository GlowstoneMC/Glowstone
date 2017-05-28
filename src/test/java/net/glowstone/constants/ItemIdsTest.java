package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests for {@link ItemIds}.
 */
@RunWith(Parameterized.class)
public class ItemIdsTest {

    private final Material material;

    public ItemIdsTest(Material material) {
        this.material = material;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCases() {
        return ParameterUtils.enumCases(Material.values());
    }

    @Test
    public void mappingExists() {
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
