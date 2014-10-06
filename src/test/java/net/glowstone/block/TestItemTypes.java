package net.glowstone.block;

import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemType;
import net.glowstone.testutils.ParameterUtils;
import org.bukkit.Material;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for the contents of {@link ItemTable}.
 */
@RunWith(Parameterized.class)
public class TestItemTypes {

    private static ItemTable table;

    @BeforeClass
    public static void staticSetup() {
        table = ItemTable.instance();
    }

    private final Material material;

    public TestItemTypes(Material material) {
        this.material = material;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCases() {
        return ParameterUtils.enumCases(Material.values());
    }

    @Test
    public void hasAllMaterials() {
        ItemType type = table.getItem(material);

        // special cases
        if (material == Material.AIR) {
            assertNull("ItemType exists for air: " + type, type);
            return;
        } else if (material == Material.LOCKED_CHEST) {
            // LOCKED_CHEST was superseded by STAINED_GLASS
            return;
        }

        // check that it exists
        assertNotNull("ItemType does not exist for " + material, type);
        // check that its block status is correct
        assertTrue("Block status mismatch between " + material + "(" + material.isBlock() + ") and " + type, (type instanceof BlockType) == material.isBlock());
        // check that material returned matches
        assertEquals("ItemType returned wrong material", material, type.getMaterial());

        // check that max stack size matches
        assertEquals("Maximum stack size was incorrect", material.getMaxStackSize(), type.getMaxStackSize());
    }

}
