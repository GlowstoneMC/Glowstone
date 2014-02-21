package net.glowstone.io.entity;

import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.*;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;


public abstract class HumanEntityStore<T extends GlowHumanEntity> extends LivingEntityStore<T> {

    public HumanEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(T entity, CompoundTag compound) {
        super.load(entity, compound);

        if (compound.is("Inventory", ListTag.class)) {
            PlayerInventory inventory = entity.getInventory();
            List<CompoundTag> items = compound.getList("Inventory", CompoundTag.class);
            inventory.setContents(NbtSerialization.readInventory(items, 0, inventory.getSize()));
            inventory.setArmorContents(NbtSerialization.readInventory(items, 100, 4));
        }
        if (compound.is("EnderItems", ListTag.class)) {
            Inventory inventory = entity.getEnderChest();
            List<CompoundTag> items = compound.getList("EnderItems", CompoundTag.class);
            inventory.setContents(NbtSerialization.readInventory(items, 0, inventory.getSize()));
        }
    }

    @Override
    public List<Tag> save(T entity) {
        List<Tag> ret = super.save(entity);

        // inventory
        List<CompoundTag> inventory;
        inventory = NbtSerialization.writeInventory(entity.getInventory().getContents(), 0);
        inventory.addAll(NbtSerialization.writeInventory(entity.getInventory().getArmorContents(), 100));
        ret.add(new ListTag<>("Inventory", TagType.COMPOUND, inventory));

        // ender items
        inventory = NbtSerialization.writeInventory(entity.getEnderChest().getContents(), 0);
        ret.add(new ListTag<>("EnderItems", TagType.COMPOUND, inventory));
        return ret;
    }
}
