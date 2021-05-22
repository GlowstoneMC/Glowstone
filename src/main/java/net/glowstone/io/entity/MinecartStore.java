package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.objects.GlowMinecart;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MinecartStore extends EntityStore<GlowMinecart> {

    private final GlowMinecart.MinecartType minecartType;

    public MinecartStore(GlowMinecart.MinecartType minecartType) {
        super(minecartType.getMinecartClass(), minecartType.getEntityType());
        this.minecartType = minecartType;
    }

    @Override
    public GlowMinecart createEntity(Location location, CompoundTag compound) {
        Function<? super Location, ? extends GlowMinecart> creator = minecartType.getCreator();
        return creator == null ? null : creator.apply(location);
    }

    @Override
    public void load(GlowMinecart entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) entity).getInventory();
            if (inventory != null) {
                tag.readCompoundList("Items",
                    items -> inventory.setContents(NbtSerialization.readInventory(
                        items, 0, inventory.getSize())));
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
                tag.putCompoundList("Items",
                    NbtSerialization.writeInventory(inv.getInventory().getContents(), 0));
            }
        }
        // todo
    }
}
