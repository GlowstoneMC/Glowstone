package net.glowstone.net.handler.play.inv;

import com.flowpowered.network.MessageHandler;
import net.glowstone.EventFactory;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.VehicleMoveMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class VehicleMoveHandler implements MessageHandler<GlowSession, VehicleMoveMessage> {

    @Override
    public void handle(GlowSession session, VehicleMoveMessage message) {
        GlowPlayer player = session.getPlayer();

        if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Vehicle)) {
            return;
        }

        GlowEntity vehicle = (GlowEntity) player.getVehicle();

        Location oldLocation = vehicle.getLocation();
        Location newLocation = oldLocation.clone();
        message.update(newLocation);

        // don't let players reach an illegal position
        if (Math.abs(newLocation.getBlockX()) > 32000000
            || Math.abs(newLocation.getBlockZ()) > 32000000) {
            session.getPlayer().kickPlayer("Illegal position");
            return;
        }

        /*
          don't let players move more than 100 blocks in a single packet
          if they move greater than 10 blocks, but less than 100, just warn
          this is NOT robust hack prevention - only to prevent client
          confusion about where its actual location is (e.g. during login)
        */
        if (Position.hasMoved(oldLocation, newLocation) && !player.isDead() && !vehicle.isDead()) {
            double distance = newLocation.distanceSquared(oldLocation);
            if (distance > 100 * 100) {
                session.getPlayer().kickPlayer("You moved too quickly :( (Hacking?)");
                return;
            } else if (distance > 100) {
                GlowServer.logger.warning(session.getPlayer().getName() + " moved too quickly!");
            }
        }

        if (!isValidMovement(vehicle, oldLocation, newLocation)) {
            vehicle.teleport(oldLocation);
            return;
        }

        // call move event if movement actually occurred and there are handlers registered
        if (!oldLocation.equals(newLocation)
            && VehicleMoveEvent.getHandlerList().getRegisteredListeners().length > 0) {
            session.getServer().getEventFactory().callEvent(
                new VehicleMoveEvent((Vehicle) vehicle, oldLocation, newLocation.clone()));
        }

        // simply update location
        vehicle.setRawLocation(newLocation);
        player.setRawLocation(vehicle.getMountLocation());

        // track movement stats
        Vector delta = newLocation.clone().subtract(oldLocation).toVector();
        delta.setX(Math.abs(delta.getX()));
        delta.setY(Math.abs(delta.getY()));
        delta.setZ(Math.abs(delta.getZ()));
        int flatDistance = (int) Math.round(Math.sqrt(
            NumberConversions.square(delta.getX()) + NumberConversions.square(delta.getZ()))
            * 100.0);

        if (vehicle instanceof Boat) {
            player.incrementStatistic(Statistic.BOAT_ONE_CM, flatDistance);
        } else if (vehicle instanceof Minecart) {
            player.incrementStatistic(Statistic.MINECART_ONE_CM, flatDistance);
        }
    }

    private boolean isValidMovement(Entity vehicle, Location oldLocation, Location newLocation) {
        if (!(vehicle instanceof Boat)) {
            return true;
        }

        boolean workOnLand = ((Boat) vehicle).getWorkOnLand();
        if (workOnLand) {
            return true;
        }

        Material type = oldLocation.getBlock().getType();
        if (type != Material.WATER && type != Material.STATIONARY_WATER) {
            return true;
        }

        return !newLocation.getBlock().getType().isSolid();
    }
}
