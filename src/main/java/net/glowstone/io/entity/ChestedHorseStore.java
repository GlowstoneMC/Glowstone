package net.glowstone.io.entity;

import java.util.List;
import java.util.function.Function;
import net.glowstone.entity.passive.GlowChestedHorse;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.ItemStack;

public class ChestedHorseStore<T extends GlowChestedHorse> extends AbstractHorseStore<T> {

    public ChestedHorseStore(Class<T> clazz, EntityType type,
        Function<Location, ? extends T> creator) {
        super(clazz, type, creator);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        AbstractHorseInventory inventory = entity.getInventory();
        if (inventory != null) {
            compound.readCompoundList(items ->
                inventory.setContents(NbtSerialization.readInventory(items, 2, 14)),"Items");
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("ChestedHorse", true);
        if (entity.getInventory() != null) {
            List<CompoundTag> items = NbtSerialization
                .writeInventory(entity.getInventory().getContents(), 2);
            tag.putCompoundList("Items", items);
        }
    }
}
