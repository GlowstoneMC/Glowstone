package net.glowstone.net.handler.play.player;

import com.flowpowered.networking.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.game.PositionRotationMessage;
import net.glowstone.net.message.play.player.PlayerUpdateMessage;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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
        if (!session.getPlayer().isTeleported()) {
            double distance = newLocation.distanceSquared(oldLocation);
            if (distance > 100 * 100) {
                GlowServer.logger.warning(session.getPlayer().getName() + " moved too quickly! (hacking)");
                return;
            } else if (distance > 100) {
                GlowServer.logger.warning(session.getPlayer().getName() + " moved too quickly!");
            }
        }


        // call move event if movement actually occurred and there are handlers registered
        if (!oldLocation.equals(newLocation) && PlayerMoveEvent.getHandlerList().getRegisteredListeners().length > 0) {
            final PlayerMoveEvent event = EventFactory.callEvent(new PlayerMoveEvent(player, oldLocation, newLocation));
            if (event.isCancelled()) {
                // tell client they're back where they started
                session.send(new PositionRotationMessage(oldLocation));
                return;
            }

            if (!event.getTo().equals(newLocation)) {
                // teleport to the set destination: fires PlayerTeleportEvent and
                // handles if the destination is in another world
                player.teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
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

        player.addMoveExhaustion(newLocation);

        // move event was not fired or did nothing, simply update location
        player.setRawLocation(newLocation);
    }
}
