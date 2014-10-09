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

        Material canonical = ItemIds.getMaterial(nameid);
        assertNotNull("Reverse mapping missing for " + material + "/" + nameid, canonical);

        // mismatch here is allowed, but log it
        if (canonical != material) {
            System.out.println(material + "(" + material.getId() + ")\t-> " + nameid + "\t-> " + canonical + "(" + canonical.getId() + ")");
        }
    }

}
