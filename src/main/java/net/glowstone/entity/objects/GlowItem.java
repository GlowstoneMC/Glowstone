package net.glowstone.entity.objects;

import net.glowstone.entity.GlowEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Item;

import net.glowstone.util.Position;
import net.glowstone.msg.Message;
import net.glowstone.msg.SpawnItemMessage;
import net.glowstone.GlowWorld;
import net.glowstone.GlowServer;

/**
 * Represents an item that is also an {@link net.glowstone.entity.GlowEntity} within the world.
 * @author Graham Edgecombe
 */
public final class GlowItem extends GlowEntity implements Item {

    /**
     * The item.
     */
    private ItemStack item;
    
    /**
     * The remaining delay until this item may be picked up.
     */
    private int pickupDelay;

    /**
     * Creates a new item entity.
     * @param world The world.
     * @param item The item.
     */
    public GlowItem(GlowServer server, GlowWorld world, ItemStack item) {
        super(server, world);
        this.item = item;
        pickupDelay = 20;
    }

    /**
     * Gets the item that this {@link GlowItem} represents.
     * @return The item.
     */
    public ItemStack getItemStack() {
        return item;
    }

    /**
     * Sets the item that this item represents.
     * @param stack The new ItemStack to use.
     */
    public void setItemStack(ItemStack stack) {
        item = stack.clone();
    }

    @Override
    public Message createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return new SpawnItemMessage(id, item, x, y, z, yaw, pitch, 0);
    }

    @Override
    public Message createUpdateMessage() {
        // TODO we can probably use some generic implementation for all of
        // these
        return null;
    }

    public int getPickupDelay() {
        return pickupDelay;
    }

    public void setPickupDelay(int delay) {
        pickupDelay = delay;
    }

}
