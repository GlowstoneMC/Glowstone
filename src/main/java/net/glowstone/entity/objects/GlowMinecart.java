package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.net.message.play.player.InteractEntityMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.minecart.*;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public abstract class GlowMinecart extends GlowEntity implements Minecart {

    private final MinecartType type;

    public GlowMinecart(Location location, MinecartType type) {
        super(location);
        this.type = type;
    }

    @Override
    public List<Message> createSpawnMessage() {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Collections.singletonList(new SpawnObjectMessage(id, getUniqueId(), 10, x, y, z, pitch, yaw, type.ordinal()));
    }

    @Override
    public boolean entityInteract(GlowPlayer player, InteractEntityMessage message) {
        if (message.getAction() == InteractEntityMessage.Action.ATTACK.ordinal()) {
            // todo: damage points
            if (this instanceof InventoryHolder) {
                InventoryHolder inv = (InventoryHolder) this;
                if (inv.getInventory() != null) {
                    for (ItemStack drop : inv.getInventory().getContents()) {
                        if (drop == null || drop.getType() == Material.AIR || drop.getAmount() < 1) {
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

    @Override
    public void setDamage(double v) {

    }

    @Override
    public double getDamage() {
        return 0;
    }

    @Override
    public double getMaxSpeed() {
        return 0;
    }

    @Override
    public void setMaxSpeed(double v) {

    }

    @Override
    public boolean isSlowWhenEmpty() {
        return false;
    }

    @Override
    public void setSlowWhenEmpty(boolean b) {

    }

    @Override
    public Vector getFlyingVelocityMod() {
        return null;
    }

    @Override
    public void setFlyingVelocityMod(Vector vector) {

    }

    @Override
    public Vector getDerailedVelocityMod() {
        return null;
    }

    @Override
    public void setDerailedVelocityMod(Vector vector) {

    }

    @Override
    public void setDisplayBlock(MaterialData materialData) {

    }

    @Override
    public MaterialData getDisplayBlock() {
        return null;
    }

    @Override
    public void setDisplayBlockOffset(int i) {

    }

    @Override
    public int getDisplayBlockOffset() {
        return 0;
    }

    @Override
    public Location getOrigin() {
        return null;
    }

    public MinecartType getMinecartType() {
        return type;
    }

    public enum MinecartType {
        RIDEABLE(Rideable.class, EntityType.MINECART, RideableMinecart.class),
        CHEST(Storage.class, EntityType.MINECART_CHEST, StorageMinecart.class),
        FURNACE(Powered.class, EntityType.MINECART_FURNACE, PoweredMinecart.class),
        TNT(Explosive.class, EntityType.MINECART_TNT, ExplosiveMinecart.class),
        SPAWNER(Spawner.class, EntityType.MINECART_MOB_SPAWNER, SpawnerMinecart.class),
        HOPPER(Hopper.class, EntityType.MINECART_HOPPER, HopperMinecart.class),
        COMMAND(null, EntityType.MINECART_COMMAND, CommandMinecart.class); // todo

        private final Class<? extends GlowMinecart> clazz;
        private final EntityType type;
        private final Class<? extends Minecart> entityClass;

        MinecartType(Class<? extends GlowMinecart> clazz, EntityType type, Class<? extends Minecart> entityClass) {
            this.clazz = clazz;
            this.type = type;
            this.entityClass = entityClass;
        }

        public Class<? extends GlowMinecart> getMinecartClass() {
            return clazz;
        }

        public EntityType getEntityType() {
            return type;
        }

        public Class<? extends Minecart> getEntityClass() {
            return entityClass;
        }
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

        private final Inventory inventory;

        public Storage(Location location) {
            super(location, MinecartType.CHEST);
            inventory = new GlowInventory(this, InventoryType.CHEST, InventoryType.CHEST.getDefaultSize(), "Minecart with Chest");
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

        @Override
        public Inventory getInventory() {
            return inventory;
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

        private boolean enabled = true;
        private final Inventory inventory;

        public Hopper(Location location) {
            super(location, MinecartType.HOPPER);
            inventory = new GlowInventory(this, InventoryType.HOPPER, InventoryType.HOPPER.getDefaultSize(), "Minecart with Hopper");
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
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
}
