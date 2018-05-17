package net.glowstone.entity.objects;

import com.flowpowered.network.Message;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.glowstone.EventFactory;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.CollectItemMessage;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.TickUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Represents an item that is also an {@link GlowEntity} within the world.
 *
 * @author Graham Edgecombe
 */
public class GlowItem extends GlowEntity implements Item {
    private static final double VERTICAL_GRAVITY_ACCEL = -0.04;

    /**
     * The number of ticks (equal to 5 minutes) that item entities should live for.
     */
    private static final int LIFETIME = TickUtil.minutesToTicks(5);

    /**
     * The remaining delay until this item may be picked up.
     */
    private int pickupDelay;

    /**
     * A player to bias this item's pickup selection towards.
     */
    private GlowPlayer biasPlayer;

    /**
     * Creates a new item entity.
     *
     * @param location The location of the entity.
     * @param item The item stack the entity is carrying.
     */
    public GlowItem(Location location, ItemStack item) {
        super(location);
        setItemStack(item);
        setBoundingBox(0.25, 0.25);
        setAirDrag(0.98);
        setGravityAccel(new Vector(0, VERTICAL_GRAVITY_ACCEL, 0));
        setApplyDragBeforeAccel(true);
        pickupDelay = 20;
    }

    private boolean getPickedUp(GlowPlayer player) {
        // todo: fire PlayerPickupItemEvent in a way that allows for 'remaining' calculations

        HashMap<Integer, ItemStack> map = player.getInventory().addItem(getItemStack());
        player
                .updateInventory(); // workaround for player editing slot & it immediately being
        // filled again
        if (!map.isEmpty()) {
            setItemStack(map.values().iterator().next());
            return false;
        } else {
            CollectItemMessage message = new CollectItemMessage(getEntityId(), player.getEntityId(),
                    getItemStack().getAmount());
            world.playSound(location, Sound.ENTITY_ITEM_PICKUP, 0.3f, (float) (1 + Math.random()));
            world.getRawPlayers().stream().filter(other -> other.canSeeEntity(this))
                    .forEach(other -> other.getSession().send(message));
            remove();
            return true;
        }
    }

    public void setBias(GlowPlayer player) {
        biasPlayer = player;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overrides

    @Override
    public EntityType getType() {
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
            if (pickupDelay < 20 && biasPlayer != null) {
                // check for the bias player
                for (Entity entity : getNearbyEntities(1, 0.5, 1)) {
                    if (entity.isDead()) {
                        continue;
                    }
                    if (entity == biasPlayer && getPickedUp((GlowPlayer) entity)) {
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
                if (entity instanceof GlowPlayer && getPickedUp((GlowPlayer) entity)) {
                    break;
                }
                if (entity instanceof GlowItem) {
                    if (entity != this && ((GlowItem) entity).getItemStack()
                            .isSimilar(getItemStack())) {
                        ItemStack clone = getItemStack().clone();
                        clone.setAmount(
                                ((GlowItem) entity).getItemStack().getAmount() + clone.getAmount());
                        entity.remove();
                        setItemStack(clone);
                    }
                }
            }
        }

        // disappear if we've lived too long
        if (getTicksLived() >= LIFETIME) {
            ItemDespawnEvent event = EventFactory.getInstance()
                    .callEvent(new ItemDespawnEvent(this, getLocation()));
            if (event.isCancelled()) {
                // Allow it to live for 5 more minutes, according to docs
                ticksLived -= LIFETIME;
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
                new SpawnObjectMessage(entityId, getUniqueId(), SpawnObjectMessage.ITEM, location),
                new EntityMetadataMessage(entityId, metadata.getEntryList()),
                // these keep the client from assigning a random velocity
                new EntityTeleportMessage(entityId, location),
                new EntityVelocityMessage(entityId, getVelocity())
        );
    }

    ////////////////////////////////////////////////////////////////////////////
    // Item stuff

    @Override
    public int getPickupDelay() {
        return pickupDelay;
    }

    @Override
    public void setPickupDelay(int delay) {
        pickupDelay = delay;
    }

    @Override
    public boolean canMobPickup() {
        // TODO: Implementation (1.12.1)
        return true;
    }

    @Override
    public void setCanMobPickup(boolean pickup) {
        // TODO: Implementation (1.12.1)
    }

    @Override
    public ItemStack getItemStack() {
        return metadata.getItem(MetadataIndex.ITEM_ITEM);
    }

    @Override
    public void setItemStack(ItemStack stack) {
        // stone is the "default state" for the item stack according to the client
        metadata.set(MetadataIndex.ITEM_ITEM,
                stack == null ? new ItemStack(Material.STONE) : stack.clone());
    }
}
