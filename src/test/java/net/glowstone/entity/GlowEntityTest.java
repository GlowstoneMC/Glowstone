package net.glowstone.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.flowpowered.network.Message;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.MutableClassToInstanceMap;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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
@RunWith(PowerMockRunner.class)
public abstract class GlowEntityTest<T extends GlowEntity> {

    public static final Answer<Object> RETURN_FIRST_ARG = invocation -> invocation.getArgument(0);

    // Mockito mocks
    protected static ItemFactory itemFactory;
    protected static GlowChunk chunk;
    protected static GlowBlock block;
    protected static GlowWorld world;
    protected static GlowServer server;
    protected static GlowScoreboardManager scoreboardManager;
    @Mock protected EventFactory eventFactory;

    // Real objects
    protected static Location location;
    protected static EntityIdManager idManager;
    protected static EntityManager entityManager;
    protected static GlowScoreboard scoreboard;
    protected Logger log;
    protected final Function<? super Location, ? extends T> entityCreator;
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
        server = Mockito.mock(GlowServer.class, Mockito.RETURNS_DEEP_STUBS);
        Bukkit.setServer(server);
        world = Mockito.mock(GlowWorld.class, Mockito.RETURNS_SMART_NULLS);
        when(world.getServer()).thenReturn(server);
        when(world.getDifficulty()).thenReturn(Difficulty.NORMAL);
        when(server.getWorlds()).thenReturn(Collections.singletonList(world));
        itemFactory = Mockito.mock(ItemFactory.class);
        chunk = Mockito.mock(GlowChunk.class);
        block = Mockito.mock(GlowBlock.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);
        when(block.getType()).thenReturn(Material.DIRT);
        when(block.getRelative(any(BlockFace.class))).thenReturn(block);
        when(world.getChunkAt(any(Location.class))).thenReturn(chunk);
        when(world.getChunkAt(any(Block.class))).thenReturn(chunk);
        when(world.getChunkAt(anyInt(),anyInt())).thenReturn(chunk);
        when(server.getItemFactory()).thenReturn(itemFactory);
        location = new Location(world, 0, 0, 0);
        entityManager = Mockito.spy(new EntityManager());
        when(world.getEntityManager()).thenReturn(entityManager);
        idManager = new EntityIdManager();
        when(server.getEntityIdManager()).thenReturn(idManager);
        scoreboardManager = Mockito.mock(GlowScoreboardManager.class, RETURNS_SMART_NULLS);
        when(server.getScoreboardManager()).thenReturn(scoreboardManager);
        scoreboard = new GlowScoreboard();
        when(scoreboardManager.getMainScoreboard()).thenReturn(scoreboard);
        when(itemFactory.ensureServerConversions(any(ItemStack.class)))
                .thenAnswer(RETURN_FIRST_ARG);
    }

    @Before
    public void setUp() throws Exception {
        log = Logger.getLogger(getClass().getSimpleName());
        when(server.getLogger()).thenReturn(log);
        MockitoAnnotations.initMocks(this);
        when(server.getEventFactory()).thenReturn(eventFactory);
        if (createEntityInSuperSetUp()) {
            entity = entityCreator.apply(location);
        }
        when(eventFactory.callEvent(any(Event.class))).thenAnswer(RETURN_FIRST_ARG);
        when(eventFactory.onEntityDamage(any(EntityDamageEvent.class))).thenAnswer(
                RETURN_FIRST_ARG);
    }

    @After
    public void tearDown() {
        // https://www.atlassian.com/blog/archives/reducing_junit_memory_usage
        log = null;
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
