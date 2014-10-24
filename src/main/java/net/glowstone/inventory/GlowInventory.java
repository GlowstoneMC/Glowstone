package net.glowstone.inventory;

import net.glowstone.constants.ItemIds;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A class which represents an inventory and the items it contains.
 */
public class GlowInventory implements Inventory {

    /**
     * The list of humans viewing this inventory.
     */
    private final Set<HumanEntity> viewers = new HashSet<>();

    /**
     * The owner of this inventory.
     */
    private final InventoryHolder owner;

    /**
     * The type of this inventory.
     */
    private final InventoryType type;

    /**
     * This inventory's contents.
     */
    private final ItemStack[] slots;

    /**
     * This inventory's slot types.
     */
    protected final SlotType[] slotTypes;

    /**
     * The inventory's name.
     */
    private String title;

    /**
     * The inventory's maximum stack size.
     */
    private int maxStackSize = 64;

    public GlowInventory(InventoryHolder owner, InventoryType type) {
        this(owner, type, type.getDefaultSize(), type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder owner, InventoryType type, int size) {
        this(owner, type, size, type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder owner, InventoryType type, int size, String title) {
        this.owner = owner;
        this.type = type;
        this.title = title;
        slots = new ItemStack[size];
        slotTypes = new SlotType[size];
        Arrays.fill(slotTypes, SlotType.CONTAINER);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Add a viewer to the inventory.
     * @param viewer The HumanEntity to add.
     */
    public void addViewer(HumanEntity viewer) {
        if (!viewers.contains(viewer)) {
            viewers.add(viewer);
        }
    }

    /**
     * Remove a viewer from the inventory.
     * @param viewer The HumanEntity to remove.
     */
    public void removeViewer(HumanEntity viewer) {
        if (viewers.contains(viewer)) {
            viewers.remove(viewer);
        }
    }

    /**
     * Get the type of the specified slot.
     * @param slot The slot number.
     * @return The SlotType of the slot.
     */
    public SlotType getSlotType(int slot) {
        if (slot < 0) return SlotType.OUTSIDE;
        return slotTypes[slot];
    }

    /**
     * Check whether it is allowed for a player to insert the given ItemStack
     * at the slot, regardless of the slot's current contents. Should return
     * false for crafting output slots or armor slots which cannot accept
     * the given item.
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemPlaceAllowed(int slot, ItemStack stack) {
        return getSlotType(slot) != SlotType.RESULT;
    }

    /**
     * Check whether, in a shift-click operation, an item of the specified type
     * may be placed in the given slot.
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemShiftClickAllowed(int slot, ItemStack stack) {
        return itemPlaceAllowed(slot, stack);
    }

    /**
     * Handle a shift click in this inventory by the specified player.
     * The default implementation distributes items from the right to the left
     * and from the bottom to the top.
     * @param player The player who clicked
     * @param view The inventory view in which was clicked
     * @param clickedSlot The slot in the view
     * @param clickedItem The item at which was clicked
     */
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot, ItemStack clickedItem) {
        clickedItem = player.getInventory().tryToFillSlots(clickedItem, 8, -1, 35, 8);
        view.setItem(clickedSlot, clickedItem);
    }

    /**
     * Tries to put the given items into the specified slots of this inventory
     * from the start slot (inclusive) to the end slot (exclusive).
     * The slots are supplied in pairs, first the start then the end slots.
     * This will first try to fill up all partial slots and if items are still
     * left after doing so, it places them into the first empty slot.
     * If no empty slot was found and there are still items left, their returned
     * from this method.
     * @param stack The items to place down
     * @param slots Pairs of start/end slots
     * @return The remaining items or {@code null} if non are remaining
     */
    public ItemStack tryToFillSlots(ItemStack stack, int...slots) {
        if (slots.length % 2 != 0) {
            throw new IllegalArgumentException("Slots must be pairs.");
        }
        // First empty slot, -1 if no empty slot was found yet
        int firstEmpty = -1;
        for (int s = 0; s < slots.length && stack.getAmount() > 0; s += 2) {
            // Iterate through all pairs of start and end slots
            int start = slots[s];
            int end = slots[s + 1];
            int delta = start < end ? 1 : -1;
            for (int i = start; i != end && stack.getAmount() > 0; i += delta) {
                // Check whether shift clicking is allowed in that slot of the inventory
                if (!itemShiftClickAllowed(i, stack)) {
                    continue;
                }

                ItemStack currentStack = getItem(i);
                if (currentStack == null) {
                    if (firstEmpty == -1) {
                        firstEmpty = i; // Found first empty slot
                    }
                } else if (currentStack.isSimilar(stack)) { // Non empty slot of similar items, try to fill stack
                    // Calculate the amount of transferable items
                    int amount = currentStack.getAmount();
                    int maxStackSize = Math.min(currentStack.getMaxStackSize(), getMaxStackSize());
                    int transfer = Math.min(stack.getAmount(), maxStackSize - amount);
                    if (transfer > 0) {
                        // And if there are any, transfer them
                        currentStack.setAmount(amount + transfer);
                        stack.setAmount(stack.getAmount() - transfer);
                    }
                    setItem(i, currentStack);
                }
            }
        }
        if (stack.getAmount() <= 0) {
            stack = null;
        }
        // If there are still items left, place them in the first empty slot (if any)
        if (stack != null && firstEmpty != -1) {
            setItem(firstEmpty, stack);
            stack = null;
        }
        return stack;
    }

    /**
     * Set the custom title of this inventory or reset it to the default.
     * @param title The new title, or null to reset.
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = type.getDefaultTitle();
        } else {
            this.title = title;
        }
    }

    /**
     * Gets the number of slots in this inventory according to the protocol.
     * Some inventories have 0 slots in the protocol, despite having slots.
     * @return The numbers of slots
     */
    public int getRawSlots() {
        return getSize();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    @Override
    public final int getSize() {
        return slots.length;
    }

    @Override
    public final InventoryType getType() {
        return type;
    }

    @Override
    public InventoryHolder getHolder() {
        return owner;
    }

    @Override
    public final String getName() {
        return title;
    }

    @Override
    public final String getTitle() {
        return title;
    }

    @Override
    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public void setMaxStackSize(int size) {
        this.maxStackSize = size;
    }

    @Override
    public List<HumanEntity> getViewers() {
        return new ArrayList<>(viewers);
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return new InventoryIterator(this);
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        if (index < 0) {
            // negative indices go from back
            index += getSize() + 1;
        }
        return new InventoryIterator(this, index);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get, Set, Add, Remove

    @Override
    public ItemStack getItem(int index) {
        return slots[index];
    }

    @Override
    public void setItem(int index, ItemStack item) {
        slots[index] = ItemIds.sanitize(item);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            ItemStack item = ItemIds.sanitize(items[i]);
            if (item == null) continue; // invalid items fail silently
            int maxStackSize = item.getType() == null ? 64 : item.getType().getMaxStackSize();
            int toAdd = item.getAmount();

            for (int j = 0; toAdd > 0 && j < getSize(); ++j) {
                // Look for existing stacks to add to
                if (slots[j] != null && slots[j].isSimilar(item)) {
                    int space = maxStackSize - slots[j].getAmount();
                    if (space < 0) continue;
                    if (space > toAdd) space = toAdd;

                    slots[j].setAmount(slots[j].getAmount() + space);
                    toAdd -= space;
                }
            }

            if (toAdd > 0) {
                // Look for empty slots to add to
                for (int j = 0; toAdd > 0 && j < getSize(); ++j) {
                    if (slots[j] == null) {
                        int num = toAdd > maxStackSize ? maxStackSize : toAdd;
                        slots[j] = item.clone();
                        slots[j].setAmount(num);
                        toAdd -= num;
                    }
                }
            }

            if (toAdd > 0) {
                // Still couldn't stash them all.
                ItemStack remaining = item.clone();
                remaining.setAmount(toAdd);
                result.put(i, remaining);
            }
        }

        return result;
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            int mat = items[i].getTypeId();
            int toRemove = items[i].getAmount();
            short damage = items[i].getDurability();

            for (int j = 0; j < getSize(); ++j) {
                // Look for stacks to remove from.
                if (slots[j] != null && slots[j].getTypeId() == mat && slots[j].getDurability() == damage) {
                    if (slots[j].getAmount() > toRemove) {
                        slots[j].setAmount(slots[j].getAmount() - toRemove);
                    } else {
                        toRemove -= slots[j].getAmount();
                        slots[j] = null;
                    }
                }
            }

            if (toRemove > 0) {
                // Couldn't remove them all.
                result.put(i, new ItemStack(mat, toRemove, damage));
            }
        }

        return result;
    }

    @Override
    public ItemStack[] getContents() {
        return slots;
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (items.length != slots.length) {
            throw new IllegalArgumentException("Length of items must be " + slots.length);
        }
        for (int i = 0; i < slots.length; ++i) {
            slots[i] = ItemIds.sanitize(items[i]);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Contains

    @Override
    public boolean contains(int materialId) {
        return first(materialId) >= 0;
    }

    @Override
    public boolean contains(Material material) {
        return first(material) >= 0;
    }

    @Override
    public boolean contains(ItemStack item) {
        return first(item) >= 0;
    }

    @Override
    public boolean contains(int materialId, int amount) {
        HashMap<Integer, ? extends ItemStack> found = all(materialId);
        int total = 0;
        for (ItemStack stack : found.values()) {
            total += stack.getAmount();
        }
        return total >= amount;
    }

    @Override
    public boolean contains(Material material, int amount) {
        return contains(material.getId(), amount);
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return contains(item.getTypeId(), amount);
    }

    @Override
    public boolean containsAtLeast(ItemStack item, int amount) {
        return false;  // todo
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find all

    @Override
    public HashMap<Integer, ItemStack> all(int materialId) {
        HashMap<Integer, ItemStack> result = new HashMap<>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i].getTypeId() == materialId) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    @Override
    public HashMap<Integer, ItemStack> all(Material material) {
        return all(material.getId());
    }

    @Override
    public HashMap<Integer, ItemStack> all(ItemStack item) {
        HashMap<Integer, ItemStack> result = new HashMap<>();
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) {
                result.put(i, slots[i]);
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find first

    @Override
    public int first(int materialId) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].getTypeId() == materialId) return i;
        }
        return -1;
    }

    @Override
    public int first(Material material) {
        return first(material.getId());
    }

    @Override
    public int first(ItemStack item) {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] != null && slots[i].equals(item)) return i;
        }
        return -1;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] == null) return i;
        }
        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Remove

    @Override
    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    @Override
    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    @Override
    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        for (Integer slot : stacks.keySet()) {
            setItem(slot, null);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Clear

    @Override
    public void clear(int index) {
        setItem(index, null);
    }

    @Override
    public void clear() {
        for (int i = 0; i < slots.length; ++i) {
            clear(i);
        }
    }

}
