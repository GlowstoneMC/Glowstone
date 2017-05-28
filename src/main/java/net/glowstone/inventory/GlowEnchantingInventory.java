package net.glowstone.inventory;

import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;

import static net.glowstone.util.Position.copyPosition;

public class GlowEnchantingInventory extends GlowInventory implements EnchantingInventory {
    private static final int ITEM_SLOT = 0;
    private static final int LAPIS_SLOT = 1;

    private final Location location;
    private final EnchantmentManager enchantmentManager;

    public GlowEnchantingInventory(Location location, GlowPlayer player) {
        super(player, InventoryType.ENCHANTING);

        getSlot(ITEM_SLOT).setType(SlotType.CRAFTING);
        getSlot(LAPIS_SLOT).setType(SlotType.CRAFTING);

        enchantmentManager = new EnchantmentManager(this, player);

        this.location = location;

        enchantmentManager.invalidate();
    }

    public void onPlayerEnchant(int clicked) {
        enchantmentManager.onPlayerEnchant(clicked);
    }

    public int getBookshelfCount() {
        int count = 0;

        Location loc = location.clone();

        for (int y = 0; y <= 1; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (z == 0 && x == 0) continue;
                    copyPosition(location, loc);
                    loc.add(x, 0, z);
                    if (loc.getBlock().isEmpty()) {
                        loc.add(0, 1, 0);
                        if (loc.getBlock().isEmpty()) {
                            copyPosition(location, loc);

                            //diagonal and straight
                            loc.add(x << 1, y, z << 1);
                            if (loc.getBlock().getType() == Material.BOOKSHELF) {
                                count++;
                            }

                            if (x != 0 && z != 0) {
                                //one block diagonal and one straight
                                copyPosition(location, loc);
                                loc.add(x << 1, y, z);
                                if (loc.getBlock().getType() == Material.BOOKSHELF) {
                                    ++count;
                                }

                                copyPosition(location, loc);
                                loc.add(x, y, z << 1);
                                if (loc.getBlock().getType() == Material.BOOKSHELF) {
                                    ++count;
                                }
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);
        enchantmentManager.invalidate();
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public int getRawSlots() {
        return 0;
    }

    @Override
    public ItemStack getItem() {
        return getItem(ITEM_SLOT);
    }

    @Override
    public void setItem(ItemStack item) {
        setItem(ITEM_SLOT, item);
    }

    @Override
    public ItemStack getSecondary() {
        return getItem(LAPIS_SLOT);
    }

    @Override
    public void setSecondary(ItemStack itemStack) {
        setItem(LAPIS_SLOT, itemStack);
    }
}
