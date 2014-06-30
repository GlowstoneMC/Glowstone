package net.glowstone.io.entity;

import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.GameMode;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

abstract class HumanEntityStore<T extends GlowHumanEntity> extends LivingEntityStore<T> {

    public HumanEntityStore(Class<T> clazz, String id) {
        super(clazz, id);
    }

    // documented at http://minecraft.gamepedia.com/Player.dat_Format
    // player data that does not correspond to HumanEntity is in PlayerStore

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isInt("playerGameType")) {
            GameMode mode = GameMode.getByValue(tag.getInt("playerGameType"));
            if (mode != null) {
                entity.setGameMode(mode);
            }
        }
        if (tag.isInt("SelectedItemSlot")) {
            entity.getInventory().setHeldItemSlot(tag.getInt("SelectedItemSlot"));
        }
        // Sleeping and SleepTimer are ignored on load.

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

        // humans don't have these properties
        tag.remove("CustomName");
        tag.remove("CustomNameVisible");
        tag.remove("Equipment");
        tag.remove("DropChances");
        tag.remove("CanPickUpLoot");
        tag.remove("PersistenceRequired");
        tag.remove("Leashed");
        tag.remove("Leash");

        tag.putInt("playerGameType", entity.getGameMode().getValue());
        tag.putInt("SelectedItemSlot", entity.getInventory().getHeldItemSlot());
        tag.putBool("Sleeping", entity.isSleeping());
        tag.putShort("SleepTimer", entity.getSleepTicks());

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
