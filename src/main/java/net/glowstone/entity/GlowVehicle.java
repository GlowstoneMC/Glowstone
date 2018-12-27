package net.glowstone.entity;

import net.glowstone.EventFactory;
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public abstract class GlowVehicle extends GlowEntity implements Vehicle {
    /**
     * Creates a vehicle and adds it to the specified world.
     *
     * @param location The location of the entity.
     */
    public GlowVehicle(Location location) {
        super(location);
        VehicleCreateEvent event = EventFactory.getInstance()
            .callEvent(new VehicleCreateEvent(this));
        if (event.isCancelled()) {
            this.remove();
            return;
        }
    }
}
