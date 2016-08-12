package net.glowstone.net.handler.play.player;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PositionRotationMessage;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class PlayerUpdateHandler implements MessageHandler<GlowSession, PlayerUpdateMessage> {

    @Override
    public void handle(GlowSession session, PlayerUpdateMessage message) {

        GlowPlayer player = session.getPlayer();

        Location oldLocation = player.getLocation();
        Location newLocation = oldLocation.clone();
        message.update(newLocation);

        // don't let players reach an illegal position
        if (Math.abs(newLocation.getBlockX()) > 32000000 || Math.abs(newLocation.getBlockZ()) > 32000000) {
            session.getPlayer().kickPlayer("Illegal position");
            return;
        }

        /*
          don't let players move more than 100 blocks in a single packet
          if they move greater than 10 blocks, but less than 100, just warn
          this is NOT robust hack prevention - only to prevent client
          confusion about where its actual location is (e.g. during login)
        */
        if (message.moved()) {
            if (player.teleportedTo != null) {
                if (newLocation.equals(player.teleportedTo)) {
                    player.endTeleport();
                    return;
                } else {
                    return; // outdated location, so skip packet
                }
            } else {
                double distance = newLocation.distanceSquared(oldLocation);
                if (distance > 100 * 100) {
                    session.getPlayer().kickPlayer("You moved too quickly :( (Hacking?)");
                    return;
                } else if (distance > 100) {
                    GlowServer.logger.warning(session.getPlayer().getName() + " moved too quickly!");
                }
            }
        }


        // call move event if movement actually occurred and there are handlers registered
        if (!oldLocation.equals(newLocation) && PlayerMoveEvent.getHandlerList().getRegisteredListeners().length > 0) {
            PlayerMoveEvent event = EventFactory.callEvent(new PlayerMoveEvent(player, oldLocation, newLocation));
            if (event.isCancelled()) {
                // tell client they're back where they started
                session.send(new PositionRotationMessage(oldLocation));
                return;
            }

            if (!event.getTo().equals(newLocation)) {
                // teleport to the set destination: fires PlayerTeleportEvent and
                // handles if the destination is in another world
                player.teleport(event.getTo(), TeleportCause.PLUGIN);
                return;
            }

            if (!Objects.equals(session.getPlayer().getLocation(), oldLocation)) {
                // plugin changed location on move event
                return;
            }
        }

        // do stuff with onGround if we need to
        if (player.isOnGround() != message.isOnGround()) {
            player.setOnGround(message.isOnGround());
        }

        // Checks if the player is still wearing the Elytra
        ItemStack chestplate = player.getInventory().getChestplate();
        boolean hasElytra = chestplate != null && chestplate.getType() == Material.ELYTRA && chestplate.getDurability() < chestplate.getType().getMaxDurability();
        if (player.isGliding() && (player.isOnGround() || !hasElytra)) {
            player.setGliding(false);
        }

        player.addMoveExhaustion(newLocation);

        // move event was not fired or did nothing, simply update location
        player.setRawLocation(newLocation);
    }
}
