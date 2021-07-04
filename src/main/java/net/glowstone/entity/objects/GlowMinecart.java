package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.GlowVehicle;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

// TODO: Implement movement and collision detection.
public abstract class GlowMinecart extends GlowVehicle implements Minecart {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;

    @Getter
    @Setter
    private volatile double damage;
    @Getter
    @Setter
    private volatile double maxSpeed;
    @Getter
    @Setter
    private volatile boolean slowWhenEmpty;
    @Getter
    @Setter
    private volatile Vector flyingVelocityMod;
    @Getter
    @Setter
    private volatile Vector derailedVelocityMod;
    @Getter
    @Setter
    private volatile MaterialData displayBlock;
    @Getter
    @Setter
    private volatile int displayBlockOffset;
    @Getter
    private final MinecartType minecartType;

    /**
     * Creates a minecart.
     *
     * @param location the location
     * @param minecartType the minecart type (i.e. the type of block carried, if any)
     */
    public GlowMinecart(Location location, MinecartType minecartType) {
        super(location);
        setSize(0.98f, 0.7f);
        setAirDrag(0.95);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        this.minecartType = minecartType;
    }

    /**
     * Factory method that creates a minecart.
     *
     * @param location the location
     * @param minecartType the minecart type (i.e. the type of block carried, if any)
     * @return The resultant minecart
     */
    public static GlowMinecart create(Location location, MinecartType minecartType) {
        return minecartType.getCreator().apply(location);
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Collections.singletonList(new SpawnObjectMessage(
                entityId, getUniqueId(), 10, location, minecartType.ordinal()));
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == InteractEntityMessage.Action.ATTACK.ordinal()) {
            // todo: damage points
            if (this instanceof InventoryHolder) {
                InventoryHolder inv = (InventoryHolder) this;
                if (inv.getInventory() != null) {
                    for (ItemStack drop : inv.getInventory().getContents()) {
                        if (drop == null || drop.getType() == Material.AIR
                                || drop.getAmount() < 1) {
                            continue;
                        }
                        GlowItem item = world.dropItemNaturally(getLocation(), drop);
                        item.setPickupDelay(30);
                        item.setBias(player);
                    }
                }
            }
            remove();
        }
        return true;
    }

    @RequiredArgsConstructor
    public enum MinecartType {
        RIDEABLE(Rideable.class, EntityType.MINECART, RideableMinecart.class, Rideable::new),
        CHEST(Storage.class, EntityType.MINECART_CHEST, StorageMinecart.class, Storage::new),
        FURNACE(Powered.class, EntityType.MINECART_FURNACE, PoweredMinecart.class, Powered
                ::new),
        TNT(Explosive.class, EntityType.MINECART_TNT, ExplosiveMinecart.class, Explosive::new),
        SPAWNER(Spawner.class, EntityType.MINECART_MOB_SPAWNER, SpawnerMinecart.class,
                Spawner::new),
        HOPPER(Hopper.class, EntityType.MINECART_HOPPER, HopperMinecart.class, Hopper::new),
        COMMAND(Command.class, EntityType.MINECART_COMMAND, CommandMinecart.class, Command::new);

        @Getter
        private final Class<? extends GlowMinecart> minecartClass;
        @Getter
        private final EntityType entityType;
        @Getter
        private final Class<? extends Minecart> entityClass;
        @Getter
        private final Function<? super Location, ? extends GlowMinecart> creator;
    }

    public static class Rideable extends GlowMinecart implements RideableMinecart {

        public Rideable(Location location) {
            super(location, MinecartType.RIDEABLE);
        }

        @Override
        public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
            super.entityInteract(player, message);
            if (message.getAction() != InteractEntityMessage.Action.INTERACT.ordinal()) {
                return false;
            }
            if (player.isSneaking()) {
                return false;
            }
            if (isEmpty()) {
                // todo: fix passengers
                // setPassenger(player);
                return true;
            }
            return false;
        }
    }

    public static class Storage extends GlowMinecart implements StorageMinecart {

        @Getter
        private final Inventory inventory;

        /**
         * Creates a minecart with a chest.
         *
         * @param location the location.
         */
        public Storage(Location location) {
            super(location, MinecartType.CHEST);
            inventory = new GlowInventory(this, InventoryType.CHEST,
                    InventoryType.CHEST.getDefaultSize(), "Minecart with Chest");
        }

        @Override
        public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
            super.entityInteract(player, message);
            if (message.getAction() != InteractEntityMessage.Action.INTERACT.ordinal()) {
                return false;
            }
            if (player.isSneaking()) {
                return false;
            }
            player.openInventory(inventory);
            return true;
        }
    }

    public static class Powered extends GlowMinecart implements PoweredMinecart {

        public Powered(Location location) {
            super(location, MinecartType.FURNACE);
        }
    }

    public static class Explosive extends GlowMinecart implements ExplosiveMinecart {

        public Explosive(Location location) {
            super(location, MinecartType.TNT);
        }
    }

    public static class Hopper extends GlowMinecart implements HopperMinecart {

        @Getter
        private final Inventory inventory;
        @Getter
        @Setter
        private boolean enabled = true;

        /**
         * Creates a minecart with a hopper.
         *
         * @param location the location
         */
        public Hopper(Location location) {
            super(location, MinecartType.HOPPER);
            inventory = new GlowInventory(this, InventoryType.HOPPER,
                    InventoryType.HOPPER.getDefaultSize(), "Minecart with Hopper");
        }

        @Override
        public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
            super.entityInteract(player, message);
            if (message.getAction() != InteractEntityMessage.Action.INTERACT.ordinal()) {
                return false;
            }
            if (player.isSneaking()) {
                return false;
            }
            player.openInventory(inventory);
            return true;
        }
    }

    public static class Spawner extends GlowMinecart implements SpawnerMinecart {

        public Spawner(Location location) {
            super(location, MinecartType.SPAWNER);
        }
    }

    public static class Command extends GlowMinecart implements CommandMinecart {
        // TODO: Behavior not implemented

        @Getter
        @Setter
        private String command;
        @Getter
        @Setter
        private String name;

        public Command(Location location) {
            super(location, MinecartType.COMMAND);
        }
    }
}
