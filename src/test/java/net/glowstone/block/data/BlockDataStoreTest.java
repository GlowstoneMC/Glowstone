package net.glowstone.block.data;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bukkit.Material;
import org.junit.Test;

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

    @Test
    public void testMutatedBlockData() {
        SingletonBlockData<Waterlogged> singleton = BlockDataStore.getBlockDataById(0);
        Waterlogged mutable = singleton.mutate();
        assertThat(mutable.isWaterlogged(), is(false));

        mutable.setWaterlogged(true);
        int newId = BlockDataStore.getBlockDataPaletteId(mutable);
        assertThat(newId, is(1));
    }

    @Test
    public void testGetBlockDataForMaterial() {
        Material material = Material.SIGN;

        BlockData data = BlockDataStore.findSingleton(material).mutate();
        assertThat(BlockDataStore.getBlockDataPaletteId(data), is(0));

        ((Waterlogged) data).setWaterlogged(true);
        assertThat(BlockDataStore.getBlockDataPaletteId(data), is(1));
    }
}
