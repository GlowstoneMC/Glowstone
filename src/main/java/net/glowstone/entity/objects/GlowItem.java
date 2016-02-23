package net.glowstone.entity.objects;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.*;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Represents an item that is also an {@link net.glowstone.entity.GlowEntity} within the world.
 * @author Graham Edgecombe
 */
public final class GlowItem extends GlowEntity implements Item {

    /**
     * The number of ticks (equal to 5 minutes) that item entities should live for.
     */
    private static final int LIFETIME = 5 * 60 * 20;

    /**
     * Velocity reduction applied each tick.
     */
    private static final double AIR_DRAG = 0.99;

    /**
     * Velocity reduction applied each tick.
     */
    private static final double LIQUID_DRAG = 0.8;

    /**
     * Gravity acceleration applied each tick.
     */
    private static final Vector GRAVITY = new Vector(0, -0.05, 0);

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
     * @param location The location of the entity.
     * @param item The item stack the entity is carrying.
     */
    public GlowItem(Location location, ItemStack item) {
        super(location);
        setItemStack(item);
        setBoundingBox(0.25, 0.25);
        pickupDelay = 20;
    }

    private boolean getPickedUp(GlowPlayer player) {
        // todo: fire PlayerPickupItemEvent in a way that allows for 'remaining' calculations

        HashMap<Integer, ItemStack> map = player.getInventory().addItem(getItemStack());
        player.updateInventory(); // workaround for player editing slot & it immediately being filled again
        if (!map.isEmpty()) {
            setItemStack(map.values().iterator().next());
            return false;
        } else {
            CollectItemMessage message = new CollectItemMessage(getEntityId(), player.getEntityId());
            world.playSound(location, Sound.ITEM_PICKUP, 0.3f, (float) (1 + Math.random()));
            world.getRawPlayers().stream().filter(other -> other.canSeeEntity(this)).forEach(other -> other.getSession().send(message));
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
                    if (entity == biasPlayer && getPickedUp((GlowPlayer) entity)) {
                        break;
                    }
                }
            }
        } else {
            // check for nearby players
            for (Entity entity : getNearbyEntities(1, 0.5, 1)) {
                if (entity instanceof GlowPlayer && getPickedUp((GlowPlayer) entity)) {
                    break;
                }
                if (entity instanceof GlowItem) {
                    if (entity != this && ((GlowItem) entity).getItemStack().isSimilar(getItemStack())) {
                        ItemStack clone = getItemStack().clone();
                        clone.setAmount(((GlowItem) entity).getItemStack().getAmount() + clone.getAmount());
                        entity.remove();
                        setItemStack(clone);
                    }
                }
            }
        }

        // disappear if we've lived too long
        if (getTicksLived() >= LIFETIME) {
            remove();
        }
    }

    @Override
    protected void pulsePhysics() {
        // simple temporary gravity - should eventually be improved to be real
        // physics and moved up to GlowEntity
        if (location.getBlock().getType().isSolid()) {
            setRawLocation(location.clone().add(0, 0.2, 0), false);
        }
        if (!location.clone().add(getVelocity()).getBlock().getType().isSolid()) {
            location.add(getVelocity());
            if (location.getBlock().isLiquid()) {
                velocity.multiply(LIQUID_DRAG);
            } else {
                velocity.multiply(AIR_DRAG);
            }
            velocity.add(GRAVITY);
        }

        super.pulsePhysics();
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
                new SpawnObjectMessage(id, SpawnObjectMessage.ITEM, x, y, z, pitch, yaw),
                new EntityMetadataMessage(id, metadata.getEntryList()),
                // these keep the client from assigning a random velocity
                new EntityTeleportMessage(id, x, y, z, yaw, pitch),
                new EntityVelocityMessage(id, getVelocity())
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
    public ItemStack getItemStack() {
        return metadata.getItem(MetadataIndex.ITEM_ITEM);
    }

    @Override
    public void setItemStack(ItemStack stack) {
        // stone is the "default state" for the item stack according to the client
        metadata.set(MetadataIndex.ITEM_ITEM, stack == null ? new ItemStack(Material.STONE) : stack.clone());
    }
}
