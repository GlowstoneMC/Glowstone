package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowMinecart;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class MinecartStore extends EntityStore<GlowMinecart> {

    private GlowMinecart.MinecartType type;

    public MinecartStore(GlowMinecart.MinecartType type) {
        super((Class<GlowMinecart>) type.getMinecartClass(), type.getName());
        this.type = type;
    }

    @Override
    public GlowMinecart createEntity(Location location, CompoundTag compound) {
        try {
            Constructor<? extends GlowMinecart> constructor = type.getMinecartClass().getConstructor(Location.class);
            return constructor.newInstance(location);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void load(GlowMinecart entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof InventoryHolder) {
            InventoryHolder inv = (InventoryHolder) entity;
            if (inv.getInventory() != null) {
                inv.getInventory().setContents(NbtSerialization.readInventory(tag.getCompoundList("Items"), 0, inv.getInventory().getSize()));
            }
        }
        // todo
    }

    @Override
    public void save(GlowMinecart entity, CompoundTag tag) {
        super.save(entity, tag);
        if (entity instanceof InventoryHolder) {
            InventoryHolder inv = (InventoryHolder) entity;
            if (inv.getInventory() != null) {
                tag.putCompoundList("Items", NbtSerialization.writeInventory(inv.getInventory().getContents(), 0));
            }
        }
        // todo
    }
}
