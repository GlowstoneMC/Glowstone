package net.glowstone.block.itemtype;

import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Set;
import net.glowstone.block.GlowBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class ItemBoatTest extends ItemTypeTest {

    @Mock(answer = RETURNS_SMART_NULLS)
    private Block waterBlock;
    @Mock(answer = RETURNS_SMART_NULLS)
    private Block airBlock;
    @Mock(answer = RETURNS_SMART_NULLS)
    private Boat boatEntity;
    private final ItemBoat boatItemHandler = new ItemBoat(TreeSpecies.ACACIA);
    private final ItemStack boatItemStack = new ItemStack(Material.BOAT_ACACIA);

    @BeforeEach
    @Override
    @SuppressWarnings("unchecked")
    public void setUp() {
        super.setUp();
        inventory.setItemInMainHand(boatItemStack);
        when(player.getTargetBlock((Set<Material>) eq(null), anyInt())).thenReturn(waterBlock);
        when(waterBlock.getType()).thenReturn(Material.STATIONARY_WATER);
        when(waterBlock.isEmpty()).thenReturn(false);
        when(waterBlock.getRelative(BlockFace.UP)).thenReturn(airBlock);
        when(waterBlock.getWorld()).thenReturn(world);
        when(airBlock.getType()).thenReturn(Material.AIR);
        when(airBlock.isEmpty()).thenReturn(true);
        when(airBlock.getLocation()).thenReturn(location);
        when(airBlock.getWorld()).thenReturn(world);
        when(world.spawn(any(Location.class), eq(Boat.class))).thenReturn(boatEntity);

    }

    @Test
    void testRightClickAir() {
        boatItemHandler.rightClickAir(player, boatItemStack);
        verify(world, times(1)).spawn(any(Location.class), eq(Boat.class));
        assertEmpty(inventory.getItemInMainHand());
    }

    @Test
    void testRightClickBlock() {
        boatItemHandler.rightClickBlock(
                player, Mockito.mock(GlowBlock.class), BlockFace.DOWN, boatItemStack,
                new Vector(0, 0, 0), EquipmentSlot.HAND);
        verify(world, times(1)).spawn(any(Location.class), eq(Boat.class));
        assertEmpty(inventory.getItemInMainHand());
    }
}