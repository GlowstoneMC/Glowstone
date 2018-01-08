package net.glowstone.io.entity;

import java.util.function.Function;
import net.glowstone.entity.objects.GlowMinecart;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;

public class MinecartStore extends EntityStore<GlowMinecart> {

    private GlowMinecart.MinecartType type;

    public MinecartStore(GlowMinecart.MinecartType type) {
        super(type.getMinecartClass(), type.getEntityType());
        this.type = type;
    }

    @Override
    public GlowMinecart createEntity(Location location, CompoundTag compound) {
        Function<? super Location, ? extends GlowMinecart> creator = type.getCreator();
        return creator == null ? null : creator.apply(location);
    }

    @Override
    public void load(GlowMinecart entity, CompoundTag tag) {
        super.load(entity, tag);
        if (entity instanceof InventoryHolder) {
            InventoryHolder inv = (InventoryHolder) entity;
            if (inv.getInventory() != null && tag.isCompoundList("Items")) {
                inv.getInventory().setContents(NbtSerialization
                        .readInventory(tag.getCompoundList("Items"), 0, inv.getInventory().getSize()));
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
