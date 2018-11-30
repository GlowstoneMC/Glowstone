package net.glowstone.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.glowstone.EventFactory;
import net.glowstone.block.BuiltinMaterialValueManager;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.MaterialValueManager;
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
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLevelChangeEvent;
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
     * Hide inherited field with this name, as a check: any reference to it in GlowPlayerTest should
     * be to {@link #entity} instead.
     */
    protected final boolean player = false;

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
    @Mock(answer = RETURNS_SMART_NULLS)
    private EventFactory eventFactory;

    // Real objects

    private static final GlowPlayerProfile profile
            = new GlowPlayerProfile("TestPlayer", UUID.randomUUID(), false);
    private GlowScheduler scheduler;
    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private File opsListFile;
    private UuidListFile opsList;
    private ItemStack fishingRodItem;
    private MaterialValueManager materialValueManager = new BuiltinMaterialValueManager();

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
        when(server.getMaterialValueManager()).thenReturn(materialValueManager);
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
        when(block.getLocation()).thenReturn(location);
        when(block.getType()).thenReturn(Material.AIR);
        when(block.getRelative(any(BlockFace.class))).thenReturn(block);
        when(block.getMaterialValues()).thenCallRealMethod();
        when(block.getWorld()).thenReturn(world);
        fishingRodItem = new ItemStack(Material.FISHING_ROD);
        entity = entityCreator.apply(location);
        entity.setItemInHand(fishingRodItem);
        entity.setDigging(null);
        entity.setLevel(1);
        when(session.getPlayer()).thenReturn(entity);
        when(world.getRawPlayers()).thenReturn(Collections.singletonList(entity));
        EventFactory.setInstance(eventFactory);
        when(eventFactory.callEvent(any(Event.class))).thenAnswer(returnsFirstArg());
    }

    private void assertCannotDigWith(@Nullable ItemStack tool) {
        entity.setItemInHand(tool);
        entity.setDigging(block);
        assertNull(entity.getDigging());
    }

    private void assertDiggingTimeEquals(long ticks) {
        Material toolType = entity.getItemInHand().getType();
        try {
            entity.setDigging(block);
            // To spend N full ticks digging, player must be pulsed N+1 times, because setDigging is
            // called in between ticks and a tick is the interval *between* two pulse() calls.
            for (long i = 0; i < ticks + 1; i++) {
                assertEquals(block, entity.getDigging());
                verify(block, never()).breakNaturally(any(ItemStack.class));
                entity.pulse();
            }
            assertNull(entity.getDigging());
            verify(block).breakNaturally(argThat(item -> item.getType() == toolType));
        } finally {
            Mockito.clearInvocations(block);
        }
    }

    @Test
    public void testDigBedrock() {
        when(block.getType()).thenReturn(Material.BEDROCK);
        for (ItemStack tool : BREAKING_TOOLS) {
            assertCannotDigWith(tool.clone());
        }
        assertCannotDigWith(null);
    }

    @Test
    public void testDigDirtNoTool() {
        when(block.getType()).thenReturn(Material.DIRT);
        assertDiggingTimeEquals(15);
    }

    @Test
    public void testDigDirtWoodenShovel() {
        entity.setItemInHand(new ItemStack(Material.WOOD_SPADE));
        when(block.getType()).thenReturn(Material.DIRT);
        assertDiggingTimeEquals(8);
    }

    @Test
    public void testDigDirtDiamondTools() {
        for (ItemStack tool : BREAKING_TOOLS) {
            entity.setItemInHand(tool.clone());
            when(block.getType()).thenReturn(Material.DIRT);
            assertDiggingTimeEquals(tool.getType() == Material.DIAMOND_SPADE ? 2 : 15);
        }
    }

    @Test
    public void testDigStoneNoTool() {
        when(block.getType()).thenReturn(Material.STONE);
        assertDiggingTimeEquals(150);
    }

    @Test
    public void testDigStoneWoodenPickaxe() {
        entity.setItemInHand(new ItemStack(Material.WOOD_PICKAXE));
        when(block.getType()).thenReturn(Material.STONE);
        assertDiggingTimeEquals(23);
    }

    @Test
    public void testDigStoneDiamondTools() {
        for (ItemStack tool : BREAKING_TOOLS) {
            entity.setItemInHand(tool.clone());
            when(block.getType()).thenReturn(Material.STONE);
            assertDiggingTimeEquals(tool.getType() == Material.DIAMOND_PICKAXE ? 6 : 150);
        }
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

    @Test
    public void testSetLevel() {
        entity.setLevel(12);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(12, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));

        assertEquals(12, entity.getLevel());
    }

    @Test
    public void testSetLevelSameLevel() {
        entity.setLevel(1);
        verify(eventFactory, never()).callEvent(any());

        assertEquals(1, entity.getLevel());
    }

    @Test
    public void testSetLevelNegativeLevel() {
        entity.setLevel(-5);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(0, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));

        assertEquals(0, entity.getLevel());
    }

    @Test
    public void testGiveExpLevels() {
        entity.giveExpLevels(14);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(15, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));
        assertEquals(15, entity.getLevel());
    }

    @Test
    public void testGiveExp() {
        entity.giveExp(20);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(2, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));
        assertEquals(2, entity.getLevel());
        assertEquals(0.17, entity.getExp(), 0.1);
        assertEquals(20, entity.getTotalExperience());
    }

    @Test
    public void testGiveExpWithoutNewLevel() {
        entity.giveExp(12);
        verify(eventFactory, never()).callEvent(any());

        assertEquals(1, entity.getLevel());
        assertEquals(0.70, entity.getExp(), 0.1);
        assertEquals(12, entity.getTotalExperience());
    }

    @Test
    public void testEnchanted() {
        entity.setExp(0.15f);
        entity.setTotalExperience(3);
        entity.enchanted(0);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(0, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));
        assertEquals(0, entity.getLevel());
        assertEquals(0.15, entity.getExp(), 0.1);
        assertEquals(3, entity.getTotalExperience());
    }

    @Test
    public void testEnchantedConsumeAll() {
        entity.setExp(0.15f);
        entity.setTotalExperience(3);
        entity.enchanted(2);
        verify(eventFactory).callEvent(argThat(input -> {
            PlayerLevelChangeEvent event = (PlayerLevelChangeEvent) input;
            assertSame(entity, event.getPlayer());
            assertEquals(0, event.getNewLevel());
            assertEquals(1, event.getOldLevel());
            return true;
        }));
        assertEquals(0, entity.getLevel());
        assertEquals(0, entity.getExp(), 0.1);
        assertEquals(0, entity.getTotalExperience());
    }
}
