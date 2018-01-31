package net.glowstone.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.ChunkManager;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.meta.profile.GlowPlayerProfile;
import net.glowstone.entity.passive.GlowFishingHook;
import net.glowstone.io.PlayerDataService.PlayerReader;
import net.glowstone.net.GlowSession;
import net.glowstone.net.SessionRegistry;
import net.glowstone.scheduler.GlowScheduler;
import net.glowstone.scheduler.WorldScheduler;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.bans.UuidListFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({GlowServer.class, GlowWorld.class, ChunkManager.class})
@RunWith(PowerMockRunner.class)
public class GlowPlayerTest {
    private static final Logger LOG = Logger.getLogger(GlowPlayerTest.class.getSimpleName());

    private final EntityIdManager idManager = new EntityIdManager();
    private final EntityManager entityManager = new EntityManager();

    // PowerMock mocks
    private final GlowWorld world = PowerMockito.mock(GlowWorld.class, Mockito.RETURNS_MOCKS);
    private final GlowServer server = PowerMockito.mock(GlowServer.class, Mockito.RETURNS_MOCKS);
    private final ChunkManager chunkManager
            = PowerMockito.mock(ChunkManager.class, Mockito.RETURNS_MOCKS);

    private GlowPlayer player;
    private Location origin;
    private final GlowPlayerProfile profile
            = new GlowPlayerProfile("TestPlayer", UUID.randomUUID());
    private GlowScheduler scheduler;
    private final SessionRegistry sessionRegistry = new SessionRegistry();
    private File opsListFile;
    private UuidListFile opsList;

    @Mock(answer = RETURNS_SMART_NULLS)
    private GlowSession session;
    @Mock(answer = RETURNS_SMART_NULLS)
    private PlayerReader reader;
    @Mock(answer = RETURNS_SMART_NULLS)
    private GlowBlock block;
    @Mock(answer = RETURNS_SMART_NULLS)
    private WorldScheduler worldScheduler;
    @Mock(answer = RETURNS_SMART_NULLS)
    private PluginManager pluginManager;
    @Mock(answer = RETURNS_SMART_NULLS)
    private GlowChunk chunk;

    @Before
    public void setUp() throws IOException {
        opsListFile = File.createTempFile("test-ops-list", "");
        opsList = new UuidListFile(opsListFile);
        when(server.getLogger()).thenReturn(LOG);
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(server);
        }
        MockitoAnnotations.initMocks(this);
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getSessionRegistry()).thenReturn(sessionRegistry);
        when(server.getEntityIdManager()).thenReturn(idManager);
        origin = new Location(world, 0, 0, 0);
        scheduler = new GlowScheduler(server, worldScheduler);
        when(session.getServer()).thenReturn(server);
        when(server.getWorlds()).thenReturn(Collections.singletonList(world));
        when(server.getScheduler()).thenReturn(scheduler);
        when(server.getOpsList()).thenReturn(opsList);
        when(world.getSpawnLocation()).thenReturn(origin);
        when(world.getServer()).thenReturn(server);
        when(world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(block);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(world.getEntityManager()).thenReturn(entityManager);
        when(world.getChunkAt(any(Location.class))).thenReturn(chunk);
        when(world.getChunkAt(any(Block.class))).thenReturn(chunk);
        when(world.getChunkAt(anyInt(),anyInt())).thenReturn(chunk);
        when(world.getChunkManager()).thenReturn(chunkManager);
        when(block.getType()).thenReturn(Material.AIR);
        when(block.getRelative(any(BlockFace.class))).thenReturn(block);
        player = new GlowPlayer(session, profile, reader);
        player.setItemInHand(new ItemStack(Material.FISHING_ROD));
        when(session.getPlayer()).thenReturn(player);
    }

    @Test
    public void testFishingContinues() {
        final GlowFishingHook fishingHook = new GlowFishingHook(origin);
        player.setCurrentFishingHook(fishingHook);
        player.pulse();
        assertSame(fishingHook, player.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsAtDistance() {
        player.setCurrentFishingHook(new GlowFishingHook(origin));
        player.teleport(new Location(world, 33, 0, 0));
        player.endTeleport();
        player.pulse();
        assertNull(player.getCurrentFishingHook());
    }

    @Test
    public void testFishingStopsWhenNoPoleHeld() {
        player.setCurrentFishingHook(new GlowFishingHook(origin));
        player.setItemInHand(InventoryUtil.createEmptyStack());
        player.pulse();
        assertNull(player.getCurrentFishingHook());
    }
}