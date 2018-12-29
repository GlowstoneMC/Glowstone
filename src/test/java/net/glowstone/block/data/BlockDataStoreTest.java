package net.glowstone.block.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unchecked")
public class BlockDataStoreTest {
    @Test
    public void testGetBlockDataById() {
        SingletonBlockData<Waterlogged> signNoWl = BlockDataStore.getBlockDataById(0);
        SingletonBlockData<Waterlogged> signYesWl = BlockDataStore.getBlockDataById(1);

        assertThat(signNoWl.mutate().isWaterlogged(), is(false));
        assertThat(signYesWl.mutate().isWaterlogged(), is(true));
        assertThat(signNoWl.getGlobalPaletteId(), is(0));
        assertThat(signYesWl.getGlobalPaletteId(), is(1));
    }

    @Test
    public void testGetIdForBlockData() {
        SingletonBlockData<Waterlogged> signNoWl = BlockDataStore.getBlockDataById(0);
        SingletonBlockData<Waterlogged> signYesWl = BlockDataStore.getBlockDataById(1);

        Waterlogged mutatedNoWl = signNoWl.mutate();
        Waterlogged mutatedYesWl = signYesWl.mutate();

        assertThat(BlockDataStore.getBlockDataPaletteId(mutatedNoWl), is(0));
        assertThat(BlockDataStore.getBlockDataPaletteId(mutatedYesWl), is(1));
    }
}
