package net.glowstone.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.ChunkManager;
import net.glowstone.chunk.ChunkManager.ChunkLock;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.entity.passive.GlowFishingHook;
import net.glowstone.io.PlayerDataService.PlayerReader;
import net.glowstone.io.PlayerStatisticIoService;
import net.glowstone.net.GlowSession;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.scheduler.WorldScheduler;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.bans.UuidListFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({Bukkit.class, ChunkManager.class})
@RunWith(PowerMockRunner.class)
public class GlowPlayerTest extends GlowHumanEntityTest<GlowPlayer> {

    /**
     * Each breakable block can be broken with one of these.
     */
    private static final List<ItemStack> BREAKING_TOOLS = ImmutableList.of(
        new ItemStack(Material.DIAMOND_AXE),
        new ItemStack(Material.DIAMOND_PICKAXE),
        new ItemStack(Material.DIAMOND_SPADE),
        new ItemStack(Material.DIAMOND_SWORD),
        new ItemStack(Material.SHEARS));

    private final ChunkManager chunkManager
            = PowerMockito.mock(ChunkManager.class, Mockito.RETURNS_MOCKS);

    // Mockito mocks
    @Mock(answer = RETURNS_SMART_NULLS)
    private static GlowSession session;
    @Mock(answer = RETURNS_SMART_NULLS)
    private static PlayerReader reader;
    @Mock(answer = RETURNS_SMART_NULLS)
    private GlowBlock block;
    @Mock(answer = RETURNS_SMART_NULLS)
    private WorldScheduler worldScheduler;
    @Mock(answer = RETURNS_SMART_NULLS)
    private PluginManager pluginManager;
    @Mock(answer = RETURNS_SMART_NULLS)
    private PlayerStatisticIoService statisticIoService;
    @Mock(answer = RETURNS_SMART_NULLS)
    private ChunkLock chunkLock;

    // Real objects

    private static final GlowPlayerProfile profile
            = new GlowPlayerProfile("TestPlayer", UUID.randomUUID(), true);
    private GlowScheduler scheduler;
    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private File opsListFile;
    private UuidListFile opsList;
    private ItemStack fishingRodItem;

    public GlowPlayerTest() {
        super(ignoredLocation -> new GlowPlayer(session, profile, reader));
    }

    @Override
    public boolean createEntityInSuperSetUp() {
        return false;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        PowerMockito.mockStatic(Bukkit.class);
        super.setUp();
        when(Bukkit.getServer()).thenReturn(server);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
        opsListFile = File.createTempFile("test-ops-list", "");
        opsList = new UuidListFile(opsListFile);
        when(server.getSessionRegistry()).thenReturn(sessionRegistry);
        when(server.getPluginManager()).thenReturn(pluginManager);
        scheduler = new GlowScheduler(server, worldScheduler);
        when(session.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(server.getOpsList()).thenReturn(opsList);
        when(server.getPlayerStatisticIoService()).thenReturn(statisticIoService);
        when(world.getSpawnLocation()).thenReturn(location);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(block);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(world.getChunkManager()).thenReturn(chunkManager);
        when(world.newChunkLock(anyString())).thenReturn(chunkLock);
        when(block.getType()).thenReturn(Material.AIR);
        when(block.getRelative(any(BlockFace.class))).thenReturn(block);
        fishingRodItem = new ItemStack(Material.FISHING_ROD);
        entity = entityCreator.apply(location);
        entity.setItemInHand(fishingRodItem);
        when(session.getPlayer()).thenReturn(entity);
    }

    private void assertCannotDig() {
        for (ItemStack tool : BREAKING_TOOLS) {
            assertCannotDigWith(tool.clone());
        }
        assertCannotDigWith(null);
    }

    private void assertCannotDigWith(@Nullable ItemStack tool) {
        entity.setItemInHand(tool);
        entity.setDigging(block);
        assertNull(entity.getDigging());
    }

    private void assertDiggingTimeEquals(long ticks) {
        entity.setDigging(block);
        for (long i = 0; i < ticks; i++) {
            assertEquals(block, entity.getDigging());
            entity.pulse();
        }
        assertNull(entity.getDigging());
        verify(block).breakNaturally(entity.getItemInHand());
    }

    @Test
    public void testDigBedrock() {
        when(block.getType()).thenReturn(Material.BEDROCK);
        assertCannotDig();
    }

    @Test
    public void testDigAir() {
        assertCannotDig();
    }

    @Test
    public void testDigLiquid() {
        when(block.getType()).thenReturn(Material.WATER);
        assertCannotDig();
    }

    @Test
    public void testDigDirtNoTool() {
        when(block.getType()).thenReturn(Material.DIRT);
        assertDiggingTimeEquals(15);
    }

    @Test
    public void testDigDirtWoodenShovel() {
        player.setItemInHand(new ItemStack(Material.WOOD_SPADE));
        when(block.getType()).thenReturn(Material.DIRT);
        assertDiggingTimeEquals(8);
    }

    @Test
    public void testDigDirtDiamondShovel() {
        player.setItemInHand(new ItemStack(Material.WOOD_SPADE));
        when(block.getType()).thenReturn(Material.DIRT);
        assertDiggingTimeEquals(2);
    }

    @Test
    public void testDigStoneNoTool() {
        when(block.getType()).thenReturn(Material.STONE);
        assertDiggingTimeEquals(150);
    }

    @Test
    public void testDigStoneWoodenPickaxe() {
        player.setItemInHand(new ItemStack(Material.WOOD_PICKAXE));
        when(block.getType()).thenReturn(Material.STONE);
        assertDiggingTimeEquals(23);
    }

    @Test
    public void testDigStoneDiamondPickaxe() {
        player.setItemInHand(new ItemStack(Material.DIAMOND_PICKAXE));
        when(block.getType()).thenReturn(Material.STONE);
        assertDiggingTimeEquals(6);
    }

    @Test
    public void testFishingContinues() {
        final GlowFishingHook fishingHook = new GlowFishingHook(location, fishingRodItem, entity);
        entity.setCurrentFishingHook(fishingHook);
        entity.pulse();
        assertSame(fishingHook, entity.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsAtDistance() {
        entity.setCurrentFishingHook(new GlowFishingHook(location, fishingRodItem, entity));
        entity.teleport(new Location(world, 33, 0, 0));
        entity.endTeleport();
        entity.pulse();
        assertNull(entity.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsWhenNoPoleHeld() {
        entity.setCurrentFishingHook(new GlowFishingHook(location, fishingRodItem, entity));
        entity.setItemInHand(InventoryUtil.createEmptyStack());
        entity.pulse();
        assertNull(entity.getCurrentFishingHook());
    }
}
