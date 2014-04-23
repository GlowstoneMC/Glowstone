package net.glowstone.io.entity;

import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;


public abstract class HumanEntityStore<T extends GlowHumanEntity> extends LivingEntityStore<T> {

    public HumanEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isList("Inventory", TagType.COMPOUND)) {
            PlayerInventory inventory = entity.getInventory();
            List<CompoundTag> items = tag.getCompoundList("Inventory");
            inventory.setContents(NbtSerialization.readInventory(items, 0, inventory.getSize()));
            inventory.setArmorContents(NbtSerialization.readInventory(items, 100, 4));
        }
        if (tag.isList("EnderItems", TagType.COMPOUND)) {
            Inventory inventory = entity.getEnderChest();
            List<CompoundTag> items = tag.getCompoundList("EnderItems");
            inventory.setContents(NbtSerialization.readInventory(items, 0, inventory.getSize()));
        }
    }

    @Override
    public void save(T entity, CompoundTag tag) {
        super.save(entity, tag);

        // inventory
        List<CompoundTag> inventory;
        inventory = NbtSerialization.writeInventory(entity.getInventory().getContents(), 0);
        inventory.addAll(NbtSerialization.writeInventory(entity.getInventory().getArmorContents(), 100));
        tag.putCompoundList("Inventory", inventory);

        // ender items
        inventory = NbtSerialization.writeInventory(entity.getEnderChest().getContents(), 0);
        tag.putCompoundList("EnderItems", inventory);
    }
}
