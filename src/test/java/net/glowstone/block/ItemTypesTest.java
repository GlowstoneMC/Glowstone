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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 * Tests for the contents of {@link ItemTable}.
 */
@RunWith(Parameterized.class)
public class ItemTypesTest {

    private static ItemTable table;

    @BeforeClass
    public static void staticSetup() {
        table = ItemTable.instance();
    }

    private final Material material;

    public ItemTypesTest(Material material) {
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
            assertThat("ItemType exists for air: " + type, type, nullValue());
            return;
        }

        // check that it exists
        assertThat("ItemType does not exist for " + material, type, notNullValue());
        // check that its block status is correct
        assertThat("Block status mismatch between " + material + "(" + material.isBlock() + ") and " + type, (type instanceof BlockType), is(material.isBlock()));
        // check that material returned matches
        assertThat("ItemType returned wrong material", type.getMaterial(), is(material));

        // check that max stack size matches
        assertThat("Maximum stack size was incorrect", type.getMaxStackSize(), is(material.getMaxStackSize()));
    }

}
