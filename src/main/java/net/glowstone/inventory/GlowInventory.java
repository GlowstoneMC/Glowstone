package net.glowstone.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.GlowServer;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

/**
 * A class which represents an inventory.
 */
public class GlowInventory implements Inventory {

    /**
     * This inventory's slots.
     */
    private List<GlowInventorySlot> slots;

    /**
     * The list of humans viewing this inventory.
     */
    private Set<HumanEntity> viewers;

    /**
     * The owner of this inventory.
     */
    @Getter
    private InventoryHolder holder;

    /**
     * The type of this inventory.
     */
    @Getter
    private InventoryType type;

    /**
     * The inventory's name.
     */
    @Getter
    private String title;

    /**
     * The inventory's maximum stack size.
     */
    @Getter
    @Setter
    private int maxStackSize = 64;

    protected GlowInventory() {
    }

    public GlowInventory(InventoryHolder holder, InventoryType type) {
        this(holder, type, type.getDefaultSize(), type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder holder, InventoryType type, int size) {
        this(holder, type, size, type.getDefaultTitle());
    }

    public GlowInventory(InventoryHolder holder, InventoryType type, int size, String title) {
        initialize(GlowInventorySlot.createList(size), new HashSet<>(), holder, type, title);
    }

    /**
     * Initializes some key components of this inventory.
     *
     * <p>This should be called in the constructor.
     *
     * @param slots List of slots this inventory has.
     * @param viewers Set for storage of current inventory viewers.
     * @param owner InventoryHolder which owns this Inventory.
     * @param type The inventory type.
     * @param title Inventory title, displayed in the client.
     */
    protected void initialize(List<GlowInventorySlot> slots, Set<HumanEntity> viewers,
            InventoryHolder owner, InventoryType type, String title) {
        this.slots = slots;
        this.viewers = viewers;
        this.holder = owner;
        this.type = type;
        this.title = title;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    /**
     * Add a viewer to the inventory.
     *
     * @param viewer The HumanEntity to add.
     */
    public void addViewer(HumanEntity viewer) {
        viewers.add(viewer);
    }

    /**
     * Remove a viewer from the inventory.
     *
     * @param viewer The HumanEntity to remove.
     */
    public void removeViewer(HumanEntity viewer) {
        viewers.remove(viewer);
    }

    /**
     * Returns the set which contains viewers.
     *
     * @return Viewers set.
     */
    public Set<HumanEntity> getViewersSet() {
        // TODO: Defensive copy
        return viewers;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    /**
     * Returns a certain slot.
     *
     * @param slot index.
     * @return The requested slot.
     */
    public GlowInventorySlot getSlot(int slot) {
        if (slot < 0 || slot > slots.size()) {
            GlowServer.logger.info("Out of bound slot: " + slot + " (max " + slots.size() + ")");
            return null;
        }
        return slots.get(slot);
    }

    /**
     * Get the type of the specified slot.
     *
     * @param slot The slot number.
     * @return The SlotType of the slot.
     */
    public SlotType getSlotType(int slot) {
        if (slot < 0) {
            return SlotType.OUTSIDE;
        }
        return slots.get(slot).getType();
    }

    /**
     * Check whether it is allowed for a player to insert the given ItemStack at the slot,
     * regardless of the slot's current contents.
     *
     * <p>Should return false for crafting output slots or armor slots which cannot accept the given
     * item.
     *
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemPlaceAllowed(int slot, ItemStack stack) {
        return getSlotType(slot) != SlotType.RESULT;
    }

    /**
     * Check whether, in a shift-click operation, an item of the specified type may be placed in the
     * given slot.
     *
     * @param slot The slot number.
     * @param stack The stack to add.
     * @return Whether the stack can be added there.
     */
    public boolean itemShiftClickAllowed(int slot, ItemStack stack) {
        return itemPlaceAllowed(slot, stack);
    }

    /**
     * Handle a shift click in this inventory by the specified player.
     *
     * <p>The default implementation distributes items from the right to the left and from the
     * bottom to the top.
     *
     * @param player The player who clicked
     * @param view The inventory view in which was clicked
     * @param clickedSlot The slot in the view
     * @param clickedItem The item at which was clicked
     */
    public void handleShiftClick(GlowPlayer player, InventoryView view, int clickedSlot,
            ItemStack clickedItem) {
        clickedItem = player.getInventory().tryToFillSlots(clickedItem, 8, -1, 35, 8);
        view.setItem(clickedSlot, clickedItem);
    }

    /**
     * Tries to put the given items into the specified slots of this inventory from the start slot
     * (inclusive) to the end slot (exclusive).
     *
     * <p>The slots are supplied in pairs, first the start then the end slots.
     *
     * <p>This will first try to fill up all partial slots and if items are still left after doing
     * so, it places them into the first empty slot.
     *
     * <p>If no empty slot was found and there are still items left, they're returned from this
     * method.
     *
     * @param stack The items to place down
     * @param slots Pairs of start/end slots
     * @return The remaining items or {@code null} if non are remaining
     */
    public ItemStack tryToFillSlots(ItemStack stack, int... slots) {
        if (slots.length % 2 != 0) {
            throw new IllegalArgumentException("Slots must be pairs.");
        }
        ItemStack maxStack = stack.clone();
        maxStack.setAmount(stack.getMaxStackSize());
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
                // Store the first empty slot
                if (firstEmpty == -1 && InventoryUtil.isEmpty(currentStack)) {
                    firstEmpty = i;
                } else if (currentStack
                        .isSimilar(stack)) { // Non empty slot of similar items, try to fill stack
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
        if (firstEmpty != -1) { // Fill empty slot
            if (stack.getAmount() > stack.getMaxStackSize()) {
                setItem(firstEmpty, maxStack);
                stack.setAmount(stack.getAmount() - stack.getMaxStackSize());
            } else {
                ItemStack finalStack = stack.clone();
                setItem(firstEmpty, finalStack);
                stack.setAmount(0);
            }
        }
        if (stack.getAmount() <= 0) {
            stack = InventoryUtil.createEmptyStack();
        }
        return stack;
    }

    /**
     * Gets the number of slots in this inventory according to the protocol.
     *
     * <p>Some inventories have 0 slots in the protocol, despite having slots.
     *
     * @return The numbers of slots
     */
    public int getRawSlots() {
        return getSize();
    }

    @Override
    public int getSize() {
        return slots.size();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic Stuff

    /**
     * Returns the whole slot list.
     *
     * @return Slot list.
     */
    public List<GlowInventorySlot> getSlots() {
        // TODO: Defensive copy
        return slots;
    }

    @Override
    public final String getName() {
        // Can't be fully Lombokified because getTitle() is identical
        return title;
    }

    /**
     * Set the custom title of this inventory or reset it to the default.
     *
     * @param title The new title, or null to reset.
     */
    public void setTitle(String title) {
        if (title == null) {
            this.title = type.getDefaultTitle();
        } else {
            this.title = title;
        }
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

    @Override
    public Location getLocation() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get, Set, Add, Remove

    @Override
    public ItemStack getItem(int index) {
        return slots.get(index).getItem();
    }

    @Override
    public void setItem(int index, ItemStack item) {
        if (index == -1) {
            return;
        }
        slots.get(index).setItem(item);
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            ItemStack remaining = addItemStack(items[i], false);

            if (!InventoryUtil.isEmpty(remaining)) {
                result.put(i, remaining);
            }
        }

        return result;
    }

    /**
     * Adds the contents of the given ItemStack to the inventory.
     *
     * @param item the ItemStack to add
     * @param ignoreMeta if true, can convert to items with different NBT data in order to stack
     *         with existing copies of those items, provided the material and damage value match
     * @return the items that couldn't be added, or an empty stack if all were added
     */
    public ItemStack addItemStack(ItemStack item, boolean ignoreMeta) {
        int maxStackSize = item.getType() == null ? 64 : item.getType().getMaxStackSize();
        int toAdd = item.getAmount();

        Iterator<GlowInventorySlot> iterator = slots.iterator();
        while (toAdd > 0 && iterator.hasNext()) {
            GlowInventorySlot slot = iterator.next();
            // Look for existing stacks to add to
            ItemStack slotItem = InventoryUtil.itemOrEmpty(slot.getItem());
            if (!InventoryUtil.isEmpty(slotItem) && compareItems(item, slotItem, ignoreMeta)) {
                int space = maxStackSize - slotItem.getAmount();
                if (space < 0) {
                    continue;
                }
                if (space > toAdd) {
                    space = toAdd;
                }

                slotItem.setAmount(slotItem.getAmount() + space);

                toAdd -= space;
            }
        }

        if (toAdd > 0) {
            // Look for empty slots to add to
            iterator = slots.iterator();
            while (toAdd > 0 && iterator.hasNext()) {
                GlowInventorySlot slot = iterator.next();
                ItemStack slotItem = slot.getItem();
                if (InventoryUtil.isEmpty(slotItem)) {
                    int num = toAdd > maxStackSize ? maxStackSize : toAdd;

                    slotItem = item.clone();
                    slotItem.setAmount(num);
                    slot.setItem(slotItem);

                    toAdd -= num;
                }
            }
        }

        if (toAdd > 0) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(toAdd);
            return remaining;
        }

        return InventoryUtil.createEmptyStack();
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        HashMap<Integer, ItemStack> result = new HashMap<>();

        for (int i = 0; i < items.length; ++i) {
            ItemStack remaining = removeItemStack(items[i], true);

            if (!InventoryUtil.isEmpty(remaining)) {
                result.put(i, remaining);
            }
        }

        return result;
    }

    /**
     * Removes the given ItemStack from the inventory.
     *
     * @param item the ItemStack to remove
     * @param ignoreMeta if true, can choose an item with different NBT data, provided the material
     *         and damage value match
     * @return the items that couldn't be removed, or an empty stack if all were removed
     */
    public ItemStack removeItemStack(ItemStack item, boolean ignoreMeta) {
        int toRemove = item.getAmount();

        Iterator<GlowInventorySlot> iterator = slots.iterator();
        while (toRemove > 0 && iterator.hasNext()) {
            GlowInventorySlot slot = iterator.next();
            ItemStack slotItem = slot.getItem();
            // Look for stacks to remove from.
            if (!InventoryUtil.isEmpty(slotItem) && compareItems(item, slotItem, ignoreMeta)) {
                if (slotItem.getAmount() > toRemove) {
                    slotItem.setAmount(slotItem.getAmount() - toRemove);
                } else {
                    toRemove -= slotItem.getAmount();
                    slot.setItem(new ItemStack(Material.AIR, 0));
                }
            }
        }

        if (toRemove > 0) {
            ItemStack remaining = new ItemStack(item);
            remaining.setAmount(toRemove);
            return remaining;
        }

        return InventoryUtil.createEmptyStack();
    }

    private boolean compareItems(ItemStack a, ItemStack b, boolean ignoreMeta) {
        if (ignoreMeta) {
            return a.getTypeId() == b.getTypeId() && a.getDurability() == b.getDurability();
        }

        return a.isSimilar(b);
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[getSize()];

        int i = 0;
        for (ItemStack itemStack : this) {
            contents[i] = InventoryUtil.itemOrEmpty(itemStack);
            i++;
        }

        return contents;
    }

    @Override
    public void setContents(ItemStack[] items) {
        if (items.length != getSize()) {
            throw new IllegalArgumentException("Length of items must be " + getSize());
        }

        Iterator<GlowInventorySlot> iterator = slots.iterator();
        for (int i = 0; i < getSize(); i++) {
            iterator.next().setItem(items[i]);
        }
    }

    @Override
    public ItemStack[] getStorageContents() {
        return InventoryUtil.NO_ITEMS;
    }

    @Override
    public void setStorageContents(ItemStack[] itemStacks) throws IllegalArgumentException {

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

        int i = 0;
        for (ItemStack slotItem : this) {
            if (!InventoryUtil.isEmpty(slotItem) && slotItem.getTypeId() == materialId) {
                result.put(i, InventoryUtil.itemOrEmpty(slotItem));
            }

            i++;
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

        int i = 0;
        for (ItemStack slotItem : this) {
            if (Objects.equals(slotItem, item)) {
                result.put(i, slotItem);
            }

            i++;
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Find first

    @Override
    public int first(int materialId) {
        int i = 0;
        for (ItemStack slotItem : this) {
            if (slotItem == null) {
                if (materialId == 0) {
                    return i;
                }
            } else if (slotItem.getTypeId() == materialId) {
                return i;
            }

            i++;
        }

        return -1;
    }

    @Override
    public int first(Material material) {
        return first(material != null ? material.getId() : 0);
    }

    @Override
    public int first(ItemStack item) {
        int i = 0;
        for (ItemStack slotItem : this) {
            if (Objects.equals(slotItem, item)) {
                return i;
            }

            i++;
        }

        return -1;
    }

    @Override
    public int firstEmpty() {
        return first((Material) null);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Remove

    @Override
    public void remove(int materialId) {
        HashMap<Integer, ? extends ItemStack> stacks = all(materialId);
        stacks.keySet().forEach(this::clear);
    }

    @Override
    public void remove(Material material) {
        HashMap<Integer, ? extends ItemStack> stacks = all(material);
        stacks.keySet().forEach(this::clear);
    }

    @Override
    public void remove(ItemStack item) {
        HashMap<Integer, ? extends ItemStack> stacks = all(item);
        stacks.keySet().forEach(this::clear);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Clear

    @Override
    public void clear(int index) {
        setItem(index, null);
    }

    @Override
    public void clear() {
        for (GlowInventorySlot slot : slots) {
            slot.setItem(InventoryUtil.createEmptyStack());
        }
    }

}
