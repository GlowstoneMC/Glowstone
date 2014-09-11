package net.glowstone.entity.objects;

import com.flowpowered.networking.Message;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.meta.MetadataIndex;
import net.glowstone.net.message.play.entity.EntityMetadataMessage;
import net.glowstone.net.message.play.entity.EntityTeleportMessage;
import net.glowstone.net.message.play.entity.EntityVelocityMessage;
import net.glowstone.net.message.play.entity.SpawnObjectMessage;
import net.glowstone.util.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
     * The remaining delay until this item may be picked up.
     */
    private int pickupDelay;

    /**
     * Creates a new item entity.
     * @param location The location of the entity.
     * @param item The item stack the entity is carrying.
     */
    public GlowItem(Location location, ItemStack item) {
        super(location);
        setItemStack(item);
        pickupDelay = 20;
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
            --pickupDelay;
        }

        // disappear if we've lived too long
        if (getTicksLived() >= LIFETIME) {
            // todo: remove();
        }
    }

    @Override
    public List<Message> createSpawnMessage() {
        int x = Position.getIntX(location);
        int y = Position.getIntY(location);
        int z = Position.getIntZ(location);

        int yaw = Position.getIntYaw(location);
        int pitch = Position.getIntPitch(location);

        return Arrays.asList(
                new SpawnObjectMessage(id, 2, x, y, z, pitch, yaw),
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
