package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowChestedHorse;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChestedHorseStore<T extends GlowChestedHorse> extends AbstractHorseStore<T> {
    public ChestedHorseStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isList("Items", TagType.COMPOUND) && entity.getInventory() != null) {
            List<CompoundTag> items = compound.getList("Items", TagType.COMPOUND);
            ItemStack[] itemStacks = NbtSerialization.readInventory(items, 2, 14);
            entity.getInventory().setContents(itemStacks);
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("ChestedHorse", true);
        if (entity.getInventory() != null) {
            List<CompoundTag> items = NbtSerialization.writeInventory(entity.getInventory().getContents(), 2);
            tag.putList("Items", TagType.COMPOUND, items);
        }
    }
}
