package net.glowstone.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.flowpowered.network.Message;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.scoreboard.GlowScoreboard;
import net.glowstone.scoreboard.GlowScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Superclass for tests of entity classes. Configures necessary mocks for subclasses.
 *
 * TODO: Create subclasses to test all types of entities.
 *
 * @param <T> the class under test
 */
@PrepareForTest({GlowWorld.class, GlowServer.class, EventFactory.class, GlowScoreboardManager.class})
@RunWith(PowerMockRunner.class)
public abstract class GlowEntityTest<T extends GlowEntity> {

    public static final Answer<Object> RETURN_FIRST_ARG = invocation -> invocation.getArgument(0);

    // PowerMock mocks
    protected GlowWorld world;
    protected static GlowServer server;
    protected GlowScoreboardManager scoreboardManager;

    // Mockito mocks
    @Mock
    protected ItemFactory itemFactory;
    @Mock(answer = RETURNS_SMART_NULLS)
    protected GlowChunk chunk;
    @Mock
    protected GlowBlock block;

    // Real objects
    protected Logger log;
    protected Location location;
    protected EntityIdManager idManager;
    protected EntityManager entityManager;
    protected final Function<? super Location, ? extends T> entityCreator;
    protected GlowScoreboard scoreboard;
    protected T entity;

    protected GlowEntityTest(Function<? super Location, ? extends T> entityCreator) {
        this.entityCreator = entityCreator;
    }

    /**
     * Override this to return false in subclasses that call super.{@link #setUp()}, if the entity
     * under test should not be created in the super method.
     *
     * @return true if GlowEntity's implementation of {@link #setUp()} is to invoke {@link
     * #entityCreator}; false otherwise.
     */
    public boolean createEntityInSuperSetUp() {
        return true;
    }

    @BeforeClass
    public static void staticSetUp() throws Exception {
        server = PowerMockito.mock(GlowServer.class, Mockito.RETURNS_DEEP_STUBS);
        Bukkit.setServer(server);
    }

    @Before
    public void setUp() throws Exception {
        log = Logger.getLogger(getClass().getSimpleName());
        when(server.getLogger()).thenReturn(log);
        world = PowerMockito.mock(GlowWorld.class, Mockito.RETURNS_SMART_NULLS);
        MockitoAnnotations.initMocks(this);
        location = new Location(world, 0, 0, 0);
        when(world.getServer()).thenReturn(server);
        entityManager = Mockito.spy(new EntityManager());
        when(world.getEntityManager()).thenReturn(entityManager);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(block.getType()).thenReturn(Material.DIRT);
        when(block.getRelative(any(BlockFace.class))).thenReturn(block);
        when(world.getChunkAt(any(Location.class))).thenReturn(chunk);
        when(world.getChunkAt(any(Block.class))).thenReturn(chunk);
        when(world.getChunkAt(anyInt(),anyInt())).thenReturn(chunk);
        when(world.getDifficulty()).thenReturn(Difficulty.NORMAL);
        when(server.getWorlds()).thenReturn(Collections.singletonList(world));
        when(server.getItemFactory()).thenReturn(itemFactory);
        idManager = new EntityIdManager();
        when(server.getEntityIdManager()).thenReturn(idManager);
        scoreboardManager = PowerMockito.mock(GlowScoreboardManager.class, RETURNS_SMART_NULLS);
        when(server.getScoreboardManager()).thenReturn(scoreboardManager);
        scoreboard = new GlowScoreboard();
        when(scoreboardManager.getMainScoreboard()).thenReturn(scoreboard);
        mockStatic(EventFactory.class);
        when(EventFactory.callEvent(any(Event.class))).thenAnswer(
                RETURN_FIRST_ARG);
        when(EventFactory.onEntityDamage(any(EntityDamageEvent.class))).thenAnswer(
                RETURN_FIRST_ARG);
        // ensureServerConversions returns its argument
        when(itemFactory.ensureServerConversions(any(ItemStack.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        if (createEntityInSuperSetUp()) {
            entity = entityCreator.apply(location);
        }
    }

    @After
    public void tearDown() {
        // https://www.atlassian.com/blog/archives/reducing_junit_memory_usage
        world = null;
        scoreboardManager = null;
        itemFactory = null;
        chunk = null;
        block = null;
        log = null;
        location = null;
        idManager = null;
        entityManager = null;
        scoreboard = null;
        entity = null;
    }

    @Test
    public void testCreateSpawnMessage() {
        List<Message> messages = entity.createSpawnMessage();
        assertFalse(messages.isEmpty());
        // Should start with an instance of one of the Spawn*Message classes
        assertTrue(messages.get(0).getClass().getSimpleName().startsWith("Spawn"));
    }
}
