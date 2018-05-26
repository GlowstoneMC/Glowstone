package net.glowstone.command.minecraft;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import net.glowstone.GlowWorld;
import net.glowstone.testutils.InMemoryBlockStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

public class TestForBlocksCommandTest extends CommandTest<TestForBlocksCommand> {
    private InMemoryBlockStorage blockStorage;
    private Player opPlayer;
    private GlowWorld world;

    public TestForBlocksCommandTest() {
        super(TestForBlocksCommand::new);
    }

    @Override
    @Before
    public void before() {
        super.before();
        blockStorage = new InMemoryBlockStorage();
        opPlayer = PowerMockito.mock(Player.class);
        world = PowerMockito.mock(GlowWorld.class);

        when(opPlayer.hasPermission("minecraft.command.testforblocks")).thenReturn(true);
        when(opPlayer.getWorld()).thenReturn(world);
        when(world.getBlockAt(any(Location.class))).then(
                (invocation) -> blockStorage.getBlockAt(invocation.getArgument(0)));
    }

    public void createCubeAt(int x, int y, int z) {
        blockStorage.setBlockType(new Location(world, x, y, z), Material.COAL_ORE);
        blockStorage.setBlockType(new Location(world, x, y, z + 1), Material.QUARTZ_ORE);
        blockStorage.setBlockType(new Location(world, x, y + 1, z), Material.LAPIS_ORE);
        blockStorage.setBlockType(new Location(world, x, y + 1, z + 1), Material.EMERALD_ORE);
        blockStorage.setBlockType(new Location(world, x + 1, y, z), Material.IRON_ORE);
        blockStorage.setBlockType(new Location(world, x + 1, y, z + 1), Material.GOLD_ORE);
        blockStorage.setBlockType(new Location(world, x + 1, y + 1, z), Material.REDSTONE_ORE);
        blockStorage.setBlockType(new Location(world, x + 1, y + 1, z + 1), Material.DIAMOND_ORE);
    }

    @Test
    public void testSuccessfulExecutionAllOverSameBlocks() {
        createCubeAt(2, 2, 2);
        command.execute(opPlayer, "label", new String[] {"2", "2", "2", "3", "3", "3", "2", "2", "2"});

        verify(opPlayer).sendMessage("8 blocks compared");
        verify(opPlayer).hasPermission("minecraft.command.testforblocks");
        verify(opPlayer, times(4)).getWorld();
        verify(world, times(16)).getBlockAt(any(Location.class));

        verifyNoMoreInteractions(opPlayer, world);
    }

    @Test
    public void testSuccessfulExecutionAllOverDifferentBlocks() {
        createCubeAt(2, 2, 2);
        createCubeAt(4, 5, 6);
        command.execute(opPlayer, "label", new String[] {"2", "2", "2", "3", "3", "3", "4", "5", "6"});

        verify(opPlayer).sendMessage("8 blocks compared");
        verify(opPlayer).hasPermission("minecraft.command.testforblocks");
        verify(opPlayer, times(4)).getWorld();
        verify(world, times(16)).getBlockAt(any(Location.class));

        verifyNoMoreInteractions(opPlayer, world);
    }

    @Test
    public void testSuccessfulExecutionMasked() {
        createCubeAt(2, 2, 2);
        blockStorage.setBlockType(new Location(world, 3, 3, 3), Material.AIR);
        createCubeAt(4, 5, 6);
        command.execute(opPlayer, "label", new String[] {"2", "2", "2", "3", "3", "3", "4", "5", "6", "masked"});

        verify(opPlayer).sendMessage("7 blocks compared");
        verify(opPlayer).hasPermission("minecraft.command.testforblocks");
        verify(opPlayer, times(4)).getWorld();
        verify(world, times(16)).getBlockAt(any(Location.class));

        verifyNoMoreInteractions(opPlayer, world);
    }

    @Test
    public void testFailedExecutionAll() {
        createCubeAt(2, 2, 2);
        createCubeAt(4, 5, 6);
        blockStorage.setBlockType(new Location(world, 5, 6, 7), Material.STONE);
        command.execute(opPlayer, "label", new String[] {"2", "2", "2", "3", "3", "3", "4", "5", "6"});

        verify(opPlayer).sendMessage(ChatColor.RED + "Source and destination are not identical");
        verify(opPlayer).hasPermission("minecraft.command.testforblocks");
        verify(opPlayer, times(4)).getWorld();
        verify(world, times(16)).getBlockAt(any(Location.class));

        verifyNoMoreInteractions(opPlayer, world);
    }

    @Test
    public void testFailedExecutionMasked() {
        createCubeAt(2, 2, 2);
        blockStorage.setBlockType(new Location(world, 2, 2, 3), Material.AIR);
        createCubeAt(4, 5, 6);
        blockStorage.setBlockType(new Location(world, 5, 6, 7), Material.STONE);
        command.execute(opPlayer, "label", new String[] {"2", "2", "2", "3", "3", "3", "4", "5", "6", "masked"});

        verify(opPlayer).sendMessage(ChatColor.RED + "Source and destination are not identical");
        verify(opPlayer).hasPermission("minecraft.command.testforblocks");
        verify(opPlayer, times(4)).getWorld();
        verify(world, times(16)).getBlockAt(any(Location.class));

        verifyNoMoreInteractions(opPlayer, world);
    }
}
