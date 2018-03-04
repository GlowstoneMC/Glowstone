package net.glowstone.block.itemtype;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.testutils.ServerShim;
import net.glowstone.util.InventoryUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Superclass for tests of {@link ItemType} subclasses. Provides a mock player with a real inventory
 * and a location in a mock world.
 */
public abstract class ItemTypeTest {
    // Mocks
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    protected GlowPlayer player;
    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    protected World world;

    // Real objects
    protected GlowPlayerInventory inventory;
    protected Location location;

    @BeforeEach
    public void setUp() {
        ServerShim.install();
        MockitoAnnotations.initMocks(this);
        world = mock(World.class, RETURNS_SMART_NULLS);
        location = new Location(world, 0, 0, 0);
        player = Mockito.mock(GlowPlayer.class, RETURNS_SMART_NULLS);
        inventory = new GlowPlayerInventory(player);
        when(player.getInventory()).thenReturn(inventory);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getLocation()).thenReturn(location);
    }

    protected static void assertEmpty(ItemStack item) {
        assertTrue(InventoryUtil.isEmpty(item),
                "Expected empty item stack, but found " + item);
    }
}
