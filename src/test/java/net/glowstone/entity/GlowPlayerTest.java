package net.glowstone.entity;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Supplier;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({GlowServer.class, GlowWorld.class, ChunkManager.class})
public class GlowPlayerTest extends GlowEntityTest<GlowPlayer> {

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
            = new GlowPlayerProfile("TestPlayer", UUID.randomUUID());
    private GlowScheduler scheduler;
    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private File opsListFile;
    private UuidListFile opsList;

    // Finally, the star of the show
    private GlowPlayer player;

    public GlowPlayerTest() {
        super(ignoredLocation -> new GlowPlayer(session, profile, reader));
    }

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();
        opsListFile = File.createTempFile("test-ops-list", "");
        opsList = new UuidListFile(opsListFile);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getSessionRegistry()).thenReturn(sessionRegistry);
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
        player = entityCreator.apply(location);
        player.setItemInHand(new ItemStack(Material.FISHING_ROD));
        when(session.getPlayer()).thenReturn(player);
    }

    @Test
    public void testFishingContinues() {
        final GlowFishingHook fishingHook = new GlowFishingHook(location);
        player.setCurrentFishingHook(fishingHook);
        player.pulse();
        assertSame(fishingHook, player.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsAtDistance() {
        player.setCurrentFishingHook(new GlowFishingHook(location));
        player.teleport(new Location(world, 33, 0, 0));
        player.endTeleport();
        player.pulse();
        assertNull(player.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsWhenNoPoleHeld() {
        player.setCurrentFishingHook(new GlowFishingHook(location));
        player.setItemInHand(InventoryUtil.createEmptyStack());
        player.pulse();
        assertNull(player.getCurrentFishingHook());
    }
}
