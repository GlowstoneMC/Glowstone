package net.glowstone.inventory;

import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowHumanEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.crafting.CraftingManager;
import net.glowstone.net.message.play.inv.HeldItemMessage;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

/**
 * An Inventory representing the items a player is holding.
 */
public class GlowPlayerInventory extends GlowInventory implements PlayerInventory, EntityEquipment {

    private static final int SIZE = InventoryType.PLAYER.getDefaultSize();

    /*
     * Armor slots
     */
    private static final int BOOTS_SLOT = 36;
    private static final int LEGGINGS_SLOT = 37;
    private static final int CHESTPLATE_SLOT = 38;
    private static final int HELMET_SLOT = 39;

    /*
     * Off hand slot
     */
    private static final int OFF_HAND_SLOT = 40;

    /**
     * The crafting inventory.
     *
     * @return The GlowCraftingInventory attached to this player
     */
    @Getter
    private final GlowCraftingInventory craftingInventory;
    /**
     * Tracker for inventory drags by this player.
     *
     * @return The DragTracker.
     */
    @Getter
    private final DragTracker dragTracker = new DragTracker();
    /**
     * The current held item slot.
     */
    @Getter
    private int heldItemSlot;
    /**
     * The human entity for this inventory, stored for location.
     */
    private GlowHumanEntity owner;

    /**
     * Creates the instance for the given player's inventory.
     *
     * @param owner the player who owns this inventory
     */
    public GlowPlayerInventory(GlowHumanEntity owner) {
        // all player inventories are ID 0
        // 36 = 4 rows of 9
        // + 4 = armor, completed inventory
        // + 1 = off hand slot
        super(owner, InventoryType.PLAYER, SIZE);
        craftingInventory = new GlowCraftingInventory(owner, InventoryType.CRAFTING);
        this.owner = owner;
        for (int i = 0; i <= 8; i++) {
            getSlot(i).setType(SlotType.QUICKBAR);
        }
        for (int i = BOOTS_SLOT; i <= HELMET_SLOT; i++) {
            getSlot(i).setType(SlotType.ARMOR);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    public static boolean canEquipInHelmetSlot(Material material) {
        return EnchantmentTarget.ARMOR_HEAD.includes(material) || material == Material.PUMPKIN
            || material == Material.SKULL_ITEM;
    }

    /**
     * Sets which hotbar slot is the main-hand item.
     *
     * @param slot the slot number, starting with 0 (1 less than the default keyboard shortcut)
     */
    public void setRawHeldItemSlot(int slot) {
        if (slot < 0 || slot > 8) {
            throw new IllegalArgumentException(slot + " not in range 0..8");
        }
        heldItemSlot = slot;
        setItemInMainHand(getItemInMainHand());  // send to player again just in case
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
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot,
        ItemStack clickedItem) {
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
                    // move items are be burnable to the input slot
                    // TODO: Use of variable (INPUT_SLOT) instead of hard coded value ?
                    clickedItem = top.tryToFillSlots(clickedItem, 0, -1);
                } else if (cm.isFuel(clickedItem.getType())) {
                    // move fuel items direct to fuel slot
                    // TODO: Use of variable (FUEL_SLOT) instead of hard coded value ?
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

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) super.getHolder();
    }

    @Override
    public ItemStack getItem(EquipmentSlot slot) {
        switch (slot) {
            case HAND:
                return getItemInMainHand();
            case OFF_HAND:
                return getItemInOffHand();
            case FEET:
                return getBoots();
            case LEGS:
                return getLeggings();
            case CHEST:
                return getChestplate();
            case HEAD:
                return getHelmet();
            default:
                return null;
        }
    }

    @Override
    public void setItem(EquipmentSlot slot, ItemStack item) {
        switch (slot) {
            case HAND:
                setItemInMainHand(item);
                break;
            case OFF_HAND:
                setItemInOffHand(item);
                break;
            case FEET:
                setBoots(item);
                break;
            case LEGS:
                setLeggings(item);
                break;
            case CHEST:
                setChestplate(item);
                break;
            case HEAD:
                setHelmet(item);
                break;
            default:
                // TODO: should this raise a warning?
                // do nothing
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Interface implementation

    @Override
    public ItemStack[] getStorageContents() {
        ItemStack[] storage = new ItemStack[36];
        for (int i = 0; i < 36; i++) {
            storage[i] = getItem(i);
        }
        return storage;
    }

    @Override
    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        if (items.length != 36) {
            throw new IllegalArgumentException("Length of player storage must be 36");
        }

        for (int i = 0; i < 36; i++) {
            setItem(i, items[i]);
        }
    }

    @Override
    public ItemStack[] getArmorContents() {
        ItemStack[] armor = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            armor[i] = getItem(BOOTS_SLOT + i);
        }
        return armor;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
        if (items.length != 4) {
            throw new IllegalArgumentException("Length of armor must be 4");
        }
        for (int i = 0; i < 4; i++) {
            setItem(BOOTS_SLOT + i, items[i]);
        }
    }

    @Override
    public ItemStack[] getExtraContents() {
        return new ItemStack[]{getItemInOffHand()};
    }

    @Override
    public void setExtraContents(ItemStack[] items) {
        if (items.length != 1) {
            throw new IllegalArgumentException("Length of extra contents must be 1");
        }
        setItemInOffHand(items[0]);
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
        return getItem(heldItemSlot).clone();
    }

    @Override
    public void setItemInMainHand(ItemStack item) {
        setItem(heldItemSlot, item);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return getItem(OFF_HAND_SLOT).clone();
    }

    @Override
    public void setItemInOffHand(ItemStack item) {
        setItem(OFF_HAND_SLOT, item);
    }

    @Override
    @Deprecated
    public ItemStack getItemInHand() {
        return getItemInMainHand();
    }

    @Override
    @Deprecated
    public void setItemInHand(ItemStack item) {
        setItemInMainHand(item);
    }

    @Override
    public void setHeldItemSlot(int slot) {
        setRawHeldItemSlot(slot);

        if (getHolder() instanceof GlowPlayer) {
            ((GlowPlayer) getHolder()).getSession().send(new HeldItemMessage(slot));
        }
    }

    @Override
    public Location getLocation() {
        return owner.getLocation();
    }

    /**
     * Remove all matching items from the inventory.
     *
     * @param type the item to remove, or null to remove everything
     * @param data the data value to match on, or null to match all data values
     * @return the number of items (not stacks) removed
     */
    public int clear(Material type, MaterialData data) {
        if (type == Material.AIR) {
            return 0;
        }
        int numCleared = 0;
        for (int i = 0; i < getSize(); ++i) {
            ItemStack stack = getItem(i);
            if (stack != null && (type == null || stack.getType() == type) && (data == null || stack
                .getData().equals(data))) {
                setItem(i, InventoryUtil.createEmptyStack());
                if (!InventoryUtil.isEmpty(stack)) {
                    // never report AIR as removed - else will report all empty slots cleared
                    numCleared += stack.getAmount(); // report # items, not # stacks removed
                }
            }
        }
        return numCleared;
    }

    @Override
    @Deprecated
    public int clear(int id, int data) {
        int numCleared = 0;
        for (int i = 0; i < getSize(); ++i) {
            ItemStack stack = getItem(i);
            if (stack != null && (id == -1 || stack.getTypeId() == id) && (data == -1
                || stack.getData().getData() == data)) {
                setItem(i, InventoryUtil.createEmptyStack());
                if (!InventoryUtil.isEmpty(stack)) {
                    // never report AIR as removed - else will report all empty slots cleared
                    numCleared += stack.getAmount(); // report # items, not # stacks removed
                }
            }
        }
        return numCleared;
    }

    @Override
    public float getItemInHandDropChance() {
        return getItemInMainHandDropChance();
    }

    @Override
    public void setItemInHandDropChance(float chance) {
        setItemInMainHandDropChance(chance);
    }

    @Override
    public float getItemInMainHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInMainHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getItemInOffHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInOffHandDropChance(float chance) {
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
