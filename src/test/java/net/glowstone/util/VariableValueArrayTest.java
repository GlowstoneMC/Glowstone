package net.glowstone.util;

import net.glowstone.block.ItemTable;

import org.bukkit.Material;
import org.junit.Test;

import static org.bukkit.Material.*;
import static org.junit.Assert.*;

public class VariableValueArrayTest {

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
    @Test
    public void testBacking() {
        // The sample case given on wiki.vg
        VariableValueArray array = new VariableValueArray(13, SAMPLE_BLOCKS.length);
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            array.set(i, SAMPLE_BLOCKS[i]);
        }
        // Tautological check
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            assertEquals(array.get(i), SAMPLE_BLOCKS[i]);
        }
        // Check the backing array
        assertEquals(array.getBacking()[0], EXPECTED_BACKING_1);
        assertEquals(array.getBacking()[1], EXPECTED_BACKING_2);
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
            assertEquals(array.get(i), SAMPLE_BLOCKS[i]);
        }
        // Now resize
        VariableValueArray resized = array.increaseBitsPerValueTo(13);
        for (int i = 0; i < SAMPLE_BLOCKS.length; i++) {
            assertEquals(resized.get(i), SAMPLE_BLOCKS[i]);
        }
    }

    @Test
    public void testCalculateNeededBits() {
        assertEquals(VariableValueArray.calculateNeededBits(0), 1);
        assertEquals(VariableValueArray.calculateNeededBits(1), 1);
        assertEquals(VariableValueArray.calculateNeededBits(2), 2);
        assertEquals(VariableValueArray.calculateNeededBits(3), 2);
        assertEquals(VariableValueArray.calculateNeededBits(4), 3);
        // ...
        assertEquals(VariableValueArray.calculateNeededBits(7), 3);
        assertEquals(VariableValueArray.calculateNeededBits(8), 4);
        // ...
        assertEquals(VariableValueArray.calculateNeededBits(Integer.MAX_VALUE), 31);
        assertEquals(VariableValueArray.calculateNeededBits(Integer.MIN_VALUE), 32);
        assertEquals(VariableValueArray.calculateNeededBits(-1), 32);
    }
}
