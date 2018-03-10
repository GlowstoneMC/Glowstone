package net.glowstone.block.itemtype;

import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.testutils.ServerShim;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * Superclass for tests of {@link ItemType} subclasses. Provides a mock player with a real inventory
 * and a location in a mock world.
 */
public abstract class ItemTypeTest {
    protected GlowPlayerInventory inventory;
    protected GlowPlayer player;
    protected World world;
    protected Location location;

    @BeforeEach
    public void setUp() {
        ServerShim.install();
        player = Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
        inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
    }

    @AfterAll
    public void tearDownClass() {
        // https://www.atlassian.com/blog/archives/reducing_junit_memory_usage
        inventory = null;
        player = null;
        world = null;
        location = null;
    }
}
