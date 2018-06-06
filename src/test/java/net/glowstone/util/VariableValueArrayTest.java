package net.glowstone.util;

import static org.bukkit.Material.DIRT;
import static org.bukkit.Material.GRASS;
import static org.bukkit.Material.GRAVEL;
import static org.bukkit.Material.STONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import net.glowstone.block.ItemTable;
import org.bukkit.Material;
import org.testng.annotations.Test;

public class VariableValueArrayTest {

    private static final int[] SAMPLE_BLOCKS = {
        block(GRASS, 0),
        block(DIRT, 0),
        block(DIRT, 0),
        block(DIRT, 1),
        block(STONE, 0),
        block(STONE, 0),
        block(STONE, 3),
        block(GRAVEL, 0),
        block(GRAVEL, 0),
        block(STONE, 0)
    };
    /**
     * The expected values for the backing array.
     */
    private static final long
        EXPECTED_BACKING_1 = 0b00000001_0000__000000011_0001__000000011_0000__000000011_0000__000000010_0000L,
        EXPECTED_BACKING_2 = 0b0000001_0000__000001101_0000__000001101_0000__000000001_0011__000000001_0000__0L;

    /**
     * Gets an ID for the given block.
     *
     * @param material The material for the block.
     * @param metadata The metadata to give the block.
     * @return The ID as put in an array for the given block.
     */
    private static int block(Material material, int metadata) {
        int id = ItemTable.instance().getBlock(material).getId();
        return (id << 4 | metadata);
    }

    @Test
    public void testBacking() {
        // The sample case given on wiki.vg
        VariableValueArray array = new VariableValueArray(13, SAMPLE_BLOCKS.length);
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            array.set(i, SAMPLE_BLOCKS[i]);
        }
        // Tautological check
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            assertThat(SAMPLE_BLOCKS[i], is(array.get(i)));
        }
        // Check the backing array
        assertThat(EXPECTED_BACKING_1, is(array.getBacking()[0]));
        assertThat(EXPECTED_BACKING_2, is(array.getBacking()[1]));
        // There is a 3rd value, but it only contains 2 bits which we don't care about
    }

    @Test
    public void testResize() {
        // All of our test values fit in an 8-bit array.
        VariableValueArray array = new VariableValueArray(8, SAMPLE_BLOCKS.length);
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            array.set(i, SAMPLE_BLOCKS[i]);
        }
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            assertThat(SAMPLE_BLOCKS[i], is(array.get(i)));
        }
        // Now resize
        VariableValueArray resized = array.increaseBitsPerValueTo(13);
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            assertThat(SAMPLE_BLOCKS[i], is(resized.get(i)));
        }
    }

    @Test
    public void testCalculateNeededBits() {
        assertThat(1, is(VariableValueArray.calculateNeededBits(0)));
        assertThat(1, is(VariableValueArray.calculateNeededBits(1)));
        assertThat(2, is(VariableValueArray.calculateNeededBits(2)));
        assertThat(2, is(VariableValueArray.calculateNeededBits(3)));
        assertThat(3, is(VariableValueArray.calculateNeededBits(4)));
        // ...
        assertThat(3, is(VariableValueArray.calculateNeededBits(7)));
        assertThat(4, is(VariableValueArray.calculateNeededBits(8)));
        // ...
        assertThat(31, is(VariableValueArray.calculateNeededBits(Integer.MAX_VALUE)));
        assertThat(32, is(VariableValueArray.calculateNeededBits(Integer.MIN_VALUE)));
        assertThat(32, is(VariableValueArray.calculateNeededBits(-1)));
    }
}
