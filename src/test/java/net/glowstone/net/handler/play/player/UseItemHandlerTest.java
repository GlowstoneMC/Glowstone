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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;

public class UseItemHandlerTest {

    @Test
    public void testRightClickAir() throws Exception {
        EventFactory origEventFactory = EventFactory.getInstance();
        try {
            // setup mocks
            GlowWorld world = Mockito.mock(GlowWorld.class);
            GlowServer server = Mockito.mock(GlowServer.class);
            EntityManager entityManager = Mockito.mock(EntityManager.class);
            EntityIdManager entityIdManager = Mockito.mock(EntityIdManager.class);
            GlowSession session = Mockito.mock(GlowSession.class);
            GlowPlayer player = Mockito.mock(GlowPlayer.class);
            GlowBlock emptyBlock = Mockito.mock(GlowBlock.class);
            GlowBlock targetBlock = Mockito.mock(GlowBlock.class);
            EventFactory eventFactory = Mockito.mock(EventFactory.class);
            EventFactory.setInstance(eventFactory);

            // world and server behaviours
            Mockito.when(world.getEntityManager()).thenReturn(entityManager);
            Mockito.when(world.getServer()).thenReturn(server);
            Mockito.when(server.getEntityIdManager()).thenReturn(entityIdManager);

            // setup items under test
            Location location = new Location(world, 1.0, 1.0, 1.0);
            Location playerLocation = new Location(world, 2.0, 2.0, 2.0);
            GlowPlayerInventory inventory = new GlowPlayerInventory(player);
            Assert.assertFalse(inventory.contains(Material.BOAT_JUNGLE, 1));

            ItemStack stack = new ItemStack(Material.BOAT_JUNGLE);
            inventory.setItemInMainHand(stack);
            Assert.assertTrue(inventory.contains(Material.BOAT_JUNGLE, 1));

            UseItemMessage message = new UseItemMessage(EquipmentSlot.HAND.ordinal());
            PlayerInteractEvent event = new PlayerInteractEvent(player,
                    Action.RIGHT_CLICK_AIR, stack, null, null);
            GlowBoat boat = new GlowBoat(location);

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
            Assert.assertTrue(inventory.contains(Material.BOAT_JUNGLE, 1));
            UseItemHandler handler = new UseItemHandler();
            handler.handle(session, message);
            Assert.assertFalse(inventory.contains(Material.BOAT_JUNGLE, 1));
        } finally {
            EventFactory.setInstance(origEventFactory);
        }
    }



}
