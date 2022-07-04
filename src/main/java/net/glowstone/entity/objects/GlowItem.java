package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.EventFactory;
import net.glowstone.entity.EntityNetworkUtil;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnEntityMessage;
import net.glowstone.util.InventoryUtil;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents an item that is also an {@link GlowEntity} within the world.
 *
 * @author Graham Edgecombe
 */
public class GlowItem extends GlowEntity implements Item {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;

    /**
     * The number of minutes that item entities should live for.
     */
    private static final int DEFAULT_HEALTH = 5;

    /**
     * The remaining delay until this item may be picked up.
     */
    @Getter
    @Setter
    private int pickupDelay;

    /**
     * A player to bias this item's pickup selection towards.
     */
    @Setter
    private GlowPlayer bias;

    @Setter
    private boolean canMobPickup;
    @Setter
    private boolean canPlayerPickup;
    @Setter
    private boolean willAge;
    @Getter
    @Setter
    private int health;

    @Getter
    @Setter
    @Nullable
    private UUID owner;
    @Getter
    @Setter
    @Nullable
    private UUID thrower;

    /**
     * Creates a new item entity.
     *
     * @param location The location of the entity.
     * @param item     The item stack the entity is carrying.
     */
    public GlowItem(Location location, ItemStack item) {
        super(location);
        setItemStack(InventoryUtil.itemOrEmpty(item));
        setBoundingBox(0.25, 0.25);
        setAirDrag(0.98);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        setApplyDragBeforeAccel(true);
        pickupDelay = 20;
        health = DEFAULT_HEALTH;
    }

    private boolean getPickedUp(LivingEntity entity) {
        int starting = getItemStack().getAmount();
        int remaining = 0;
        if (entity instanceof InventoryHolder) {
            HashMap<Integer, ItemStack> map =
                ((InventoryHolder) entity).getInventory().addItem(getItemStack());
            if (entity instanceof GlowPlayer) {
                // TODO: PlayerAttemptPickupItemEvent
                GlowPlayer player = ((GlowPlayer) entity);
                // workaround for player editing slot & it immediately being filled again
                player.updateInventory();
            }
            if (!map.isEmpty()) {
                ItemStack remainingItem = map.get(0);
                setItemStack(remainingItem);
                remaining = remainingItem.getAmount();
            }
            EntityPickupItemEvent entityPickupEvent =
                new EntityPickupItemEvent(entity, this, remaining);
            EventFactory.getInstance().callEvent(entityPickupEvent);
            if (remaining > 0) {
                return false;
            }
        }
        entity.playPickupItemAnimation(this, starting - remaining);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overrides
    @Override
    public @NotNull EntityType getType() {
        return EntityType.DROPPED_ITEM;
    }

    @Override
    public void pulse() {
        super.pulse();

        // decrement pickupDelay if it's less than the NBT maximum
        if (pickupDelay > 0) {
            if (pickupDelay < Short.MAX_VALUE) {
                --pickupDelay;
            }
            if (pickupDelay < 20 && bias != null) {
                // check for the bias player
                for (Entity entity : getNearbyEntities(1, 0.5, 1)) {
                    if (entity.isDead()) {
                        continue;
                    }
                    if (entity == bias && getPickedUp((LivingEntity) entity)) {
                        break;
                    }
                }
            }
        } else {
            // check for nearby players
            for (Entity entity : getNearbyEntities(1, 0.5, 1)) {
                if (entity.isDead()) {
                    continue;
                }
                boolean pickedUp = false;
                if (canMobPickup && entity instanceof LivingEntity || entity instanceof Player) {
                    pickedUp = getPickedUp(((LivingEntity) entity));
                }
                if (pickedUp) {
                    break;
                }
                if (entity instanceof GlowItem) {
                    if (entity != this && ((GlowItem) entity).getItemStack()
                        .isSimilar(getItemStack())) {
                        ItemStack clone = getItemStack().clone();

                        ItemMergeEvent event = EventFactory.getInstance()
                            .callEvent(new ItemMergeEvent((GlowItem) entity, this));

                        if (!event.isCancelled()) {
                            clone.setAmount(
                                ((GlowItem) entity).getItemStack().getAmount()
                                    + clone.getAmount());
                            entity.remove();
                            setItemStack(clone);
                        }
                    }
                }
            }
        }

        // disappear if we've lived too long
        if (getTicksLived() % TickUtil.minutesToTicks(1) == 0 && --health <= 0) {
            ItemDespawnEvent event = EventFactory.getInstance()
                .callEvent(new ItemDespawnEvent(this, getLocation()));
            if (event.isCancelled()) {
                // Allow it to live for 5 more minutes, according to docs
                health += DEFAULT_HEALTH;
                return;
            }
            remove();
        }
    }

    @Override
    protected void pulsePhysics() {
        if (location.getBlock().getType().isSolid()) {
            setRawLocation(location.clone().add(0, 0.2, 0), false);
        }

        super.pulsePhysics();
    }

    @Override
    public List<Message> createSpawnMessage() {
        return Arrays.asList(
            new SpawnEntityMessage(entityId, getUniqueId(),
                EntityNetworkUtil.getObjectId(EntityType.DROPPED_ITEM), location),
            new EntityMetadataMessage(entityId, metadata.getEntryList()),
            // these keep the client from assigning a random velocity
            new EntityTeleportMessage(entityId, location),
            new EntityVelocityMessage(entityId, getVelocity())
        );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Item stuff

    @Override
    public @NotNull ItemStack getItemStack() {
        return metadata.getItem(MetadataIndex.ITEM_ITEM);
    }

    @Override
    public void setItemStack(@NotNull ItemStack stack) {
        // stone is the "default state" for the item stack according to the client
        metadata.set(MetadataIndex.ITEM_ITEM, InventoryUtil.itemOrEmpty(stack).clone());
    }

    @Override
    public void setUnlimitedLifetime(boolean unlimited) {
        willAge = !unlimited;
    }

    @Override
    public boolean isUnlimitedLifetime() {
        return !willAge;
    }

    @Override
    public boolean canMobPickup() {
        return canMobPickup;
    }

    @Override
    public boolean canPlayerPickup() {
        return canPlayerPickup;
    }

    @Override
    public boolean willAge() {
        return willAge;
    }
}
