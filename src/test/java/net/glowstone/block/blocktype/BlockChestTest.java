package net.glowstone.block.blocktype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.junit.Test;

public class BlockChestTest {

    private static final BlockFace[] SIDES = new BlockFace[] {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

    @Test
    public void canPlaceSingleChestBesideDoubleChest() {
        BlockChest blockChest = new BlockChest();
        GlowBlock placement = blockWithAirNeighbors(Material.AIR);
        GlowBlock doubleChestLeft = blockWithAirNeighbors(Material.CHEST);
        GlowBlock doubleChestRight = blockWithAirNeighbors(Material.CHEST);

        connect(placement, BlockFace.NORTH, doubleChestLeft);
        connect(doubleChestLeft, BlockFace.SOUTH, placement);
        connect(doubleChestLeft, BlockFace.EAST, doubleChestRight);
        connect(doubleChestRight, BlockFace.WEST, doubleChestLeft);

        assertTrue(blockChest.canPlaceAt(null, placement, BlockFace.UP));
    }

    @Test
    public void cannotPlaceChestBetweenTwoAttachableSingleChests() {
        BlockChest blockChest = new BlockChest();
        GlowBlock placement = blockWithAirNeighbors(Material.AIR);
        GlowBlock northChest = blockWithAirNeighbors(Material.CHEST);
        GlowBlock eastChest = blockWithAirNeighbors(Material.CHEST);

        connect(placement, BlockFace.NORTH, northChest);
        connect(placement, BlockFace.EAST, eastChest);
        connect(northChest, BlockFace.SOUTH, placement);
        connect(eastChest, BlockFace.WEST, placement);

        assertFalse(blockChest.canPlaceAt(null, placement, BlockFace.UP));
    }

    private static GlowBlock blockWithAirNeighbors(Material material) {
        GlowBlock block = mock(GlowBlock.class);
        when(block.getType()).thenReturn(material);

        for (BlockFace face : SIDES) {
            GlowBlock air = mock(GlowBlock.class);
            when(air.getType()).thenReturn(Material.AIR);
            when(block.getRelative(face)).thenReturn(air);
        }

        return block;
    }

    private static void connect(GlowBlock source, BlockFace face, GlowBlock target) {
        when(source.getRelative(face)).thenReturn(target);
    }
}
