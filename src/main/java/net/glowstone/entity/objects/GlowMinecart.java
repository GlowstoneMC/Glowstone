package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
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
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
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
import org.bukkit.loot.LootTable;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO: Implement movement and collision detection.
public abstract class GlowMinecart extends GlowVehicle implements Minecart {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;
    @Getter
    private final MinecartType minecartType;
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
    // TODO: use material air for null
    @Getter
    @Setter
    private BlockData displayBlockData;
    @Getter
    @Setter
    private volatile int displayBlockOffset;

    /**
     * Creates a minecart.
     *
     * @param location     the location
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
     * @param location     the location
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
    public @NotNull Material getMinecartMaterial() {
        return Optional.ofNullable(minecartType).map(MinecartType::getMaterial)
            .orElse(Material.MINECART);
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
        RIDEABLE(Rideable.class, EntityType.MINECART, RideableMinecart.class, Rideable::new,
            Material.MINECART),
        CHEST(Storage.class, EntityType.MINECART_CHEST, StorageMinecart.class, Storage::new,
            Material.CHEST_MINECART),
        FURNACE(Powered.class, EntityType.MINECART_FURNACE, PoweredMinecart.class, Powered
            ::new, Material.FURNACE_MINECART),
        TNT(Explosive.class, EntityType.MINECART_TNT, ExplosiveMinecart.class, Explosive::new,
            Material.TNT_MINECART),
        SPAWNER(Spawner.class, EntityType.MINECART_MOB_SPAWNER, SpawnerMinecart.class,
            Spawner::new, Material.MINECART),
        HOPPER(Hopper.class, EntityType.MINECART_HOPPER, HopperMinecart.class, Hopper::new,
            Material.HOPPER_MINECART),
        COMMAND(Command.class, EntityType.MINECART_COMMAND, CommandMinecart.class, Command::new,
            Material.COMMAND_BLOCK_MINECART);

        @Getter
        private final Class<? extends GlowMinecart> minecartClass;
        @Getter
        private final EntityType entityType;
        @Getter
        private final Class<? extends Minecart> entityClass;
        @Getter
        private final Function<? super Location, ? extends GlowMinecart> creator;
        @Getter
        private final Material material;
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

        @Override
        public @NotNull Entity getEntity() {
            return this;
        }

        // TODO: 1.13: lootable
        @Override
        public boolean isRefillEnabled() {
            return false;
        }

        @Override
        public boolean hasBeenFilled() {
            return false;
        }

        @Override
        public boolean hasPlayerLooted(@NotNull UUID player) {
            return false;
        }

        @Override
        public @Nullable Long getLastLooted(@NotNull UUID player) {
            return null;
        }

        @Override
        public boolean setHasPlayerLooted(@NotNull UUID player, boolean looted) {
            return false;
        }

        @Override
        public boolean hasPendingRefill() {
            return false;
        }

        @Override
        public long getLastFilled() {
            return 0;
        }

        @Override
        public long getNextRefill() {
            return 0;
        }

        @Override
        public long setNextRefill(long refillAt) {
            return 0;
        }

        @Override
        public @Nullable LootTable getLootTable() {
            return null;
        }

        @Override
        public void setLootTable(@Nullable LootTable table) {

        }

        @Override
        public long getSeed() {
            return 0;
        }

        @Override
        public void setSeed(long seed) {

        }
    }

    public static class Powered extends GlowMinecart implements PoweredMinecart {

        @Getter
        @Setter
        private int fuel = 0;

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

        @Override
        public @NotNull Entity getEntity() {
            return this;
        }

        // TODO: 1.13: lootable
        @Override
        public boolean isRefillEnabled() {
            return false;
        }

        @Override
        public boolean hasBeenFilled() {
            return false;
        }

        @Override
        public boolean hasPlayerLooted(@NotNull UUID player) {
            return false;
        }

        @Override
        public @Nullable Long getLastLooted(@NotNull UUID player) {
            return null;
        }

        @Override
        public boolean setHasPlayerLooted(@NotNull UUID player, boolean looted) {
            return false;
        }

        @Override
        public boolean hasPendingRefill() {
            return false;
        }

        @Override
        public long getLastFilled() {
            return 0;
        }

        @Override
        public long getNextRefill() {
            return 0;
        }

        @Override
        public long setNextRefill(long refillAt) {
            return 0;
        }

        @Override
        public @Nullable LootTable getLootTable() {
            return null;
        }

        @Override
        public void setLootTable(@Nullable LootTable table) {

        }

        @Override
        public long getSeed() {
            return 0;
        }

        @Override
        public void setSeed(long seed) {

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
