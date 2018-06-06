package net.glowstone.util;

import static org.hamcrest.CoreMatchers.is;
import static org.testng.AssertJUnit.assertThat;

import org.bukkit.util.BlockVector;
import org.testng.annotations.Test;

public class PositionTest {

    @Test
    public void testGetPositionAsLong() throws Exception {
        BlockVector blockVector = new BlockVector(-5, 15, -20);
        long position = Position.getPosition(blockVector);
        assertThat(position, is(-1373315792916L));
    }

    @Test
    public void testGetPositionAsBlockVector() throws Exception {
        BlockVector blockVector = new BlockVector(-5, 15, -20);
        long position = -1373315792916L;

        assertThat(Position.getPosition(position), is(blockVector));
    }
}
