package net.glowstone.net.handler.play.player;

import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityIdManager;
import net.glowstone.entity.EntityManager;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.objects.GlowBoat;
import net.glowstone.inventory.GlowPlayerInventory;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.player.UseItemMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

public class UseItemHandlerTest {

    private EventFactory actualEventFactory;

    private GlowWorld world;
    private GlowServer server;
    private EntityManager entityManager;
    private EntityIdManager entityIdManager;
    private GlowSession session;
    private GlowPlayer player;
    private GlowBlock emptyBlock;
    private GlowBlock targetBlock;
    private EventFactory eventFactory;

    @Before
    public void setupMocks() {
        // cache the event factory
        actualEventFactory = EventFactory.getInstance();
        // install the mock event factory
        eventFactory = Mockito.mock(EventFactory.class);
        Mockito.when(eventFactory.callEvent(any(Event.class))).thenAnswer(returnsFirstArg());
        EventFactory.setInstance(eventFactory);

        // create mocks
        world = Mockito.mock(GlowWorld.class);
        server = Mockito.mock(GlowServer.class);
        entityManager = Mockito.mock(EntityManager.class);
        entityIdManager = Mockito.mock(EntityIdManager.class);
        session = Mockito.mock(GlowSession.class);
        player = Mockito.mock(GlowPlayer.class);
        emptyBlock = Mockito.mock(GlowBlock.class);
        targetBlock = Mockito.mock(GlowBlock.class);

        // world and server behaviours
        Mockito.when(world.getEntityManager()).thenReturn(entityManager);
        Mockito.when(world.getServer()).thenReturn(server);
        Mockito.when(server.getEntityIdManager()).thenReturn(entityIdManager);
    }

    @Test
    public void testRightClickAir() {
        // prepare objects under test
        Location location = new Location(world, 1.0, 1.0, 1.0);
        Location playerLocation = new Location(world, 2.0, 2.0, 2.0);
        GlowPlayerInventory inventory = new GlowPlayerInventory(player);
        ItemStack stack = new ItemStack(Material.JUNGLE_BOAT);
        GlowBoat boat = new GlowBoat(location);
        inventory.setItemInMainHand(stack);
        UseItemMessage message = new UseItemMessage(EquipmentSlot.HAND.ordinal());
        PlayerInteractEvent event = new PlayerInteractEvent(player,
                Action.RIGHT_CLICK_AIR, stack, null, null);

        // prepare mock behaviours
        Mockito.when(session.getPlayer()).thenReturn(player);
        Mockito.when(player.getInventory()).thenReturn(inventory);
        Mockito.when(player.getLocation()).thenReturn(playerLocation);
        Mockito.when(player.getTargetBlock((Set<Material>) null,
                5)).thenReturn(targetBlock);
        Mockito.when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        Mockito.when(eventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR,
                EquipmentSlot.HAND)).thenReturn(event);

        Mockito.when(emptyBlock.isEmpty()).thenReturn(Boolean.TRUE);
        Mockito.when(emptyBlock.getLocation()).thenReturn(location);
        Mockito.when(targetBlock.isEmpty()).thenReturn(Boolean.FALSE);
        Mockito.when(targetBlock.getRelative(BlockFace.UP)).thenReturn(emptyBlock);
        Mockito.when(targetBlock.getWorld()).thenReturn(world);
        Mockito.when(world.spawn(location, Boat.class)).thenReturn(boat);

        // run test
        Assert.assertTrue(inventory.contains(Material.JUNGLE_BOAT, 1));
        UseItemHandler handler = new UseItemHandler();
        handler.handle(session, message);
        // after calling use item and creating a boat, the inventory no longer contains a boat
        Assert.assertFalse(inventory.contains(Material.JUNGLE_BOAT, 1));
    }


    @After
    public void tearDown() {
        // restore the original event factory
        EventFactory.setInstance(actualEventFactory);
    }


}
