package net.glowstone.inventory;

import net.glowstone.GlowServer;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.crafting.CraftingManager;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * An Inventory representing the items a player is holding.
 */
public class GlowPlayerInventory extends GlowInventory implements PlayerInventory, EntityEquipment {

    private static final int SIZE = 36;

    private static final int BOOTS_SLOT = 36;
    private static final int LEGGINGS_SLOT = 37;
    private static final int CHESTPLATE_SLOT = 38;
    private static final int HELMET_SLOT = 39;

    /**
     * The armor contents.
     */
    private final ItemStack[] armor = new ItemStack[4];

    /**
     * The crafting inventory.
     */
    private final GlowCraftingInventory crafting;
    /**
     * Tracker for inventory drags.
     */
    private final DragTracker tracker = new DragTracker();
    /**
     * The current held item slot.
     */
    private int heldSlot = 0;

    public GlowPlayerInventory(GlowHumanEntity owner) {
        // all player inventories are ID 0
        // 36 = 4 rows of 9
        // + 4 = armor, completed inventory
        super(owner, InventoryType.PLAYER, SIZE);
        crafting = new GlowCraftingInventory(owner, InventoryType.CRAFTING);
        for (int i = 0; i < 9; i++) {
            getSlot(i).setType(InventoryType.SlotType.QUICKBAR);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    public static boolean canEquipInHelmetSlot(Material material) {
        return EnchantmentTarget.ARMOR_HEAD.includes(material) || material == Material.PUMPKIN || material == Material.SKULL_ITEM;
    }

    /**
     * Get the crafting inventory.
     *
     * @return The GlowCraftingInventory attached to this player
     */
    public GlowCraftingInventory getCraftingInventory() {
        return crafting;
    }

    public void setRawHeldItemSlot(int slot) {
        if (slot < 0 || slot > 8)
            throw new IllegalArgumentException(slot + " not in range 0..8");
        heldSlot = slot;
        setItemInHand(getItemInHand());  // send to player again just in case
    }

    @Override
    public InventoryType.SlotType getSlotType(int slot) {
        if (slot >= SIZE && slot - SIZE < 4) {
            return InventoryType.SlotType.ARMOR;
        } else {
            return super.getSlotType(slot);
        }
    }

    @Override
    public boolean itemPlaceAllowed(int slot, ItemStack stack) {
        if (slot == BOOTS_SLOT) {
            return EnchantmentTarget.ARMOR_FEET.includes(stack);
        }
        if (slot == LEGGINGS_SLOT) {
            return EnchantmentTarget.ARMOR_LEGS.includes(stack);
        }
        if (slot == CHESTPLATE_SLOT) {
            return EnchantmentTarget.ARMOR_TORSO.includes(stack);
        }
        if (slot == HELMET_SLOT) {
            return canEquipInHelmetSlot(stack.getType());
        }
        return super.itemPlaceAllowed(slot, stack);
    }

    @Override
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot, ItemStack clickedItem) {
        GlowInventory top = (GlowInventory) view.getTopInventory();

        // If this is the default inventory try to equip the item as armor first
        if (GlowInventoryView.isDefault(view)) {
            clickedItem = tryToFillSlots(clickedItem, 36, 40);
        }

        // Check whether the top inventory allows shift clicking in any of it's slots
        boolean topAllowsShiftClick = false;
        for (int i = 0; i < top.getSize(); i++) {
            if (top.itemShiftClickAllowed(i, clickedItem)) {
                topAllowsShiftClick = true;
                break;
            }
        }

        if (topAllowsShiftClick) {
            if (top.getType().equals(InventoryType.FURNACE)) {
                CraftingManager cm = ((GlowServer) Bukkit.getServer()).getCraftingManager();
                if (cm.getFurnaceRecipe(clickedItem) != null) {
                    // move items are be burnable to the input slot  TODO: Use of variable (INPUT_SLOT) instead of hard coded value ?
                    clickedItem = top.tryToFillSlots(clickedItem, 0, -1);
                } else if (cm.isFuel(clickedItem.getType())) {
                    // move fuel items direct to fuel slot   TODO: Use of variable (FUEL_SLOT) instead of hard coded value ?
                    clickedItem = top.tryToFillSlots(clickedItem, 1, -1);
                } else {
                    // switch them between hotbar and main inventory depending on where they are now
                    if (view.convertSlot(clickedSlot) < 9 || view.convertSlot(clickedSlot) >= 36) {
                        // move from hotbar and armor to main inventory
                        clickedItem = tryToFillSlots(clickedItem, 9, 36);
                    } else {
                        // move from main inventory to hotbar
                        clickedItem = tryToFillSlots(clickedItem, 0, 9);
                    }
                }
            } else {
                // move items to the top inventory
                clickedItem = top.tryToFillSlots(clickedItem, 0, top.getSize());
            }
        } else {
            // switch them between hotbar and main inventory depending on where they are now
            if (view.convertSlot(clickedSlot) < 9 || view.convertSlot(clickedSlot) >= 36) {
                // move from hotbar and armor to main inventory
                clickedItem = tryToFillSlots(clickedItem, 9, 36);
            } else {
                // move from main inventory to hotbar
                clickedItem = tryToFillSlots(clickedItem, 0, 9);
            }
        }

        view.setItem(clickedSlot, clickedItem);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Overrides

    /**
     * Get the DragTracker associated with this player.
     *
     * @return The DragTracker.
     */
    public DragTracker getDragTracker() {
        return tracker;
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) super.getHolder();
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index >= SIZE) {
            armor[index - SIZE] = item;
        } else {
            super.setItem(index, item);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Interface implementation

    @Override
    public ItemStack getItem(int index) {
        if (index >= SIZE) {
            return armor[index - SIZE];
        } else {
            return super.getItem(index);
        }
    }

    @Override
    public ItemStack[] getArmorContents() {
        return armor;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Length of armor must be 4");
        }
        for (int i = 0; i < 4; ++i) {
            setItem(SIZE + i, items[i]);
        }
    }

    @Override
    public ItemStack getHelmet() {
        return getItem(HELMET_SLOT);
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        setItem(HELMET_SLOT, helmet);
    }

    @Override
    public ItemStack getChestplate() {
        return getItem(CHESTPLATE_SLOT);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        setItem(CHESTPLATE_SLOT, chestplate);
    }

    @Override
    public ItemStack getLeggings() {
        return getItem(LEGGINGS_SLOT);
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        setItem(LEGGINGS_SLOT, leggings);
    }

    @Override
    public ItemStack getBoots() {
        return getItem(BOOTS_SLOT);
    }

    @Override
    public void setBoots(ItemStack boots) {
        setItem(BOOTS_SLOT, boots);
    }

    @Override
    public ItemStack getItemInMainHand() {
        return null;
    }

    @Override
    public void setItemInMainHand(ItemStack itemStack) {

    }

    @Override
    public ItemStack getItemInOffHand() {
        return null;
    }

    @Override
    public void setItemInOffHand(ItemStack itemStack) {

    }

    @Override
    public ItemStack getItemInHand() {
        return getItem(heldSlot) == null ? new ItemStack(Material.AIR, 0) : getItem(heldSlot);
    }

    @Override
    public void setItemInHand(ItemStack stack) {
        setItem(heldSlot, stack);
    }

    @Override
    public int getHeldItemSlot() {
        return heldSlot;
    }

    @Override
    public void setHeldItemSlot(int slot) {
        setRawHeldItemSlot(slot);

        if (getHolder() instanceof GlowPlayer) {
            ((GlowPlayer) getHolder()).getSession().send(new HeldItemMessage(slot));
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // EntityEquipment implementation

    @Override
    public int clear(int id, int data) {
        int numCleared = 0;
        for (int i = 0; i < getSize(); ++i) {
            ItemStack stack = getItem(i);
            if (stack != null && (stack.getTypeId() == id || id == -1) && (stack.getDurability() == data || data == -1)) {
                setItem(i, null);
                if (stack.getType() != Material.AIR) {
                    // never report AIR as removed - else will report all empty slots cleared
                    numCleared += stack.getAmount(); // report # items, not # stacks removed
                }
            }
        }
        return numCleared;
    }

    @Override
    public float getItemInHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getHelmetDropChance() {
        return 1;
    }

    @Override
    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getChestplateDropChance() {
        return 1;
    }

    @Override
    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getLeggingsDropChance() {
        return 1;
    }

    @Override
    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getBootsDropChance() {
        return 1;
    }

    @Override
    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }
}
