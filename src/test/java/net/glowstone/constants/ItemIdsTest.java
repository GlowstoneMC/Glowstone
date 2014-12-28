package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Material;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

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
        if (material == Material.LOCKED_CHEST) {
            return;
        }

        String nameid = ItemIds.getName(material);
        assertNotNull("Identifier missing for " + material, nameid);
        if (!nameid.startsWith("minecraft:")) {
            fail("Identifier '" + nameid + "' does not start with 'minecraft:'");
        }

        Material item = ItemIds.getItem(nameid);
        Material block = ItemIds.getBlock(nameid);
        String base = "Material " + material + "\t-> \"" + nameid + "\"\t-> ";
        if (material.isBlock()) {
            assertNotNull(base + "block, has no block entry", block);
            assertEquals("wrong block material", material, block);
            if (item != material) {
                System.out.println(base + "item: " + item);
            }
        } else {
            assertNotNull(base + "item, has no item entry", item);
            assertEquals("wrong item material", material, item);
            if (block == material) {
                fail(base + "not block, but maps to block: " + block);
            }
        }
    }

}
