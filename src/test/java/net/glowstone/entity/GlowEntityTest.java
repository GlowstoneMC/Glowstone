package net.glowstone.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Answers.RETURNS_SMART_NULLS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
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
import net.glowstone.chunk.GlowChunk;
import net.glowstone.scheduler.GlowScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
@PrepareForTest({GlowWorld.class, GlowServer.class, EventFactory.class})
@RunWith(PowerMockRunner.class)
public abstract class GlowEntityTest<T extends GlowEntity> {

    // Mocks
    protected final GlowWorld world = PowerMockito.mock(GlowWorld.class, Mockito.RETURNS_SMART_NULLS);
    protected final GlowServer server = PowerMockito.mock(GlowServer.class, Mockito.RETURNS_SMART_NULLS);
    @Mock
    protected ItemFactory itemFactory;
    @Mock(answer = RETURNS_SMART_NULLS)
    protected GlowChunk chunk;

    // Real objects
    protected final Logger log = Logger.getLogger(getClass().getSimpleName());
    protected Location location;
    protected final EntityIdManager idManager = new EntityIdManager();
    protected final EntityManager entityManager = new EntityManager();
    protected final Function<Location, ? extends T> entityCreator;

    protected GlowEntityTest(Function<Location, ? extends T> entityCreator) {
        this.entityCreator = entityCreator;
    }

    @Before
    public void setUp() throws IOException {
        when(server.getLogger()).thenReturn(log);
        if (Bukkit.getServer() == null) {
            Bukkit.setServer(server);
        }
        MockitoAnnotations.initMocks(this);
        location = new Location(world, 0, 0, 0);
        when(world.getServer()).thenReturn(server);
        when(world.getEntityManager()).thenReturn(entityManager);
        when(world.getChunkAt(any(Location.class))).thenReturn(chunk);
        when(world.getChunkAt(any(Block.class))).thenReturn(chunk);
        when(world.getChunkAt(anyInt(),anyInt())).thenReturn(chunk);
        when(world.getDifficulty()).thenReturn(Difficulty.NORMAL);
        when(server.getWorlds()).thenReturn(Collections.singletonList(world));
        when(server.getItemFactory()).thenReturn(itemFactory);
        when(server.getEntityIdManager()).thenReturn(idManager);
        mockStatic(EventFactory.class);
        when(EventFactory.callEvent(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // ensureServerConversions returns its argument
        when(itemFactory.ensureServerConversions(any(ItemStack.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void testCreateSpawnMessage() {
        T entity = entityCreator.apply(location);
        List<Message> messages = entity.createSpawnMessage();
        assertFalse(messages.isEmpty());
        // Should start with an instance of one of the Spawn*Message classes
        assertTrue(messages.get(0).getClass().getSimpleName().startsWith("Spawn"));
    }
}
