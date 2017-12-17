package net.glowstone.io.entity;

import java.util.List;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.config.ServerConfig;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

abstract class HumanEntityStore<T extends GlowHumanEntity> extends LivingEntityStore<T> {

    public HumanEntityStore(Class<T> clazz, EntityType type) {
        super(clazz, type);
    }

    // documented at http://minecraft.gamepedia.com/Player.dat_Format
    // player data that does not correspond to HumanEntity is in PlayerStore

    @Override
    public void load(T entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isInt("XpSeed")) {
            entity.setXpSeed(tag.getInt("XpSeed"));
        }

        if (tag.isInt("playerGameType")) {
            GlowServer server = (GlowServer) Bukkit.getServer();
            if (!server.getConfig().getBoolean(ServerConfig.Key.FORCE_GAMEMODE)) {
                GameMode mode = GameMode.getByValue(tag.getInt("playerGameType"));
                if (mode != null) {
                    entity.setGameMode(mode);
                }
            } else {
                entity.setGameMode(server.getDefaultGameMode());
            }
        }
        if (tag.isInt("SelectedItemSlot")) {
            entity.getInventory().setHeldItemSlot(tag.getInt("SelectedItemSlot"));
        }
        // Sleeping and SleepTimer are ignored on load.

        if (tag.isList("Inventory", TagType.COMPOUND)) {
            PlayerInventory inventory = entity.getInventory();
            List<CompoundTag> items = tag.getCompoundList("Inventory");
            inventory.setStorageContents(
                NbtSerialization.readInventory(items, 0, inventory.getSize() - 5));
            inventory.setArmorContents(NbtSerialization.readInventory(items, 100, 4));
            inventory.setExtraContents(NbtSerialization.readInventory(items, -106, 1));
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
        tag.remove("HandItems");
        tag.remove("ArmorItems");
        tag.remove("HandDropChances");
        tag.remove("ArmorDropChances");
        tag.remove("CanPickUpLoot");
        tag.remove("PersistenceRequired");
        tag.remove("Leashed");
        tag.remove("Leash");

        tag.putInt("playerGameType", entity.getGameMode().getValue());
        tag.putInt("SelectedItemSlot", entity.getInventory().getHeldItemSlot());
        tag.putBool("Sleeping", entity.isSleeping());
        tag.putShort("SleepTimer", entity.getSleepTicks());
        tag.putInt("XpSeed", entity.getXpSeed());

        // inventory
        List<CompoundTag> inventory;
        inventory = NbtSerialization.writeInventory(entity.getInventory().getStorageContents(), 0);
        inventory
            .addAll(NbtSerialization.writeInventory(entity.getInventory().getArmorContents(), 100));
        inventory.add(NbtSerialization.writeItem(entity.getInventory().getItemInOffHand(), -106));
        tag.putCompoundList("Inventory", inventory);

        // ender items
        inventory = NbtSerialization.writeInventory(entity.getEnderChest().getContents(), 0);
        tag.putCompoundList("EnderItems", inventory);
    }
}
