package net.glowstone.block;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Iterator;
import net.glowstone.TestUtils;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemType;
import org.bukkit.Material;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for the contents of {@link ItemTable}.
 */
public class ItemTypesTest {

    private static ItemTable table;

    @DataProvider(name = "Material")
    public static Iterator<Object[]> materials() {
        return TestUtils.enumAsDataProvider(Material.class);
    }

    @BeforeClass
    public static void staticSetup() {
        table = ItemTable.instance();
    }

    @Test(dataProvider = "Material")
    public void hasAllMaterials(Material material) {
        ItemType type = table.getItem(material);

        // special cases
        if (material == Material.AIR) {
            assertThat("ItemType exists for air: " + type, type, nullValue());
            return;
        }

        // check that it exists
        assertThat("ItemType does not exist for " + material, type, notNullValue());
        // check that its block status is correct
        assertThat("Block status mismatch between " + material + "(" + material.isBlock() + ") and "
            + type, (type instanceof BlockType), is(material.isBlock()));
        // check that material returned matches
        assertThat("ItemType returned wrong material", type.getMaterial(), is(material));

        // check that max stack size matches
        assertThat("Maximum stack size was incorrect", type.getMaxStackSize(),
            is(material.getMaxStackSize()));
    }

}
