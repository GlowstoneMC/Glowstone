package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.bukkit.Material;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/**
 * Tests for {@link ItemIds}.
 */
// TODO: load all blocks and items from 1.13
@Disabled
public class ItemIdsTest {

    @EnumSource(Material.class)
    @ParameterizedTest
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
