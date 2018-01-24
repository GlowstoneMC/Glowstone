package net.glowstone.inventory;

import java.util.List;
import net.glowstone.util.collection.SuperList;
import net.glowstone.util.collection.SuperSet;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

/**
 * Inventory which delegate to other Inventory objects.
 */
public class GlowSuperInventory extends GlowInventory {

    private List<GlowInventory> parents;

    protected GlowSuperInventory() {
    }

    public GlowSuperInventory(List<GlowInventory> parents, InventoryHolder owner,
        InventoryType type) {
        initialize(parents, owner, type);
    }

    public GlowSuperInventory(List<GlowInventory> parents, InventoryHolder owner,
        InventoryType type, String title) {
        initialize(parents, owner, type, title);
    }

    // Why these weird initialize methods, instead of handling everything
    // in the constructors?
    //
    // Well, the subclass GlowDoubleChestInventory owner must be a
    // DoubleChest. However, a DoubleChest takes a DoubleChestInventory as the
    // only constructor parameter. However, using "this" inside a super call
    // isn't possible (Java compiler complains), so stuff like
    // "super(new DoubleChest(this), ...)" can't be used.
    //
    // Using "this" after the super call inside the constructor is possible,
    // so I'm using these pseudo-constructors.
    protected void initialize(List<GlowInventory> parents, InventoryHolder owner,
        InventoryType type) {
        initialize(parents, owner, type, type.getDefaultTitle());
    }

    protected void initialize(List<GlowInventory> parents, InventoryHolder owner,
        InventoryType type, String title) {
        SuperList<GlowInventorySlot> slots = new SuperList<>();
        SuperSet<HumanEntity> viewers = new SuperSet<>();

        for (GlowInventory parent : parents) {
            slots.getParents().add(parent.getSlots());
            viewers.getParents().add(parent.getViewersSet());
        }

        initialize(slots, viewers, owner, type, title);
        this.parents = parents;
    }

    public List<GlowInventory> getParents() {
        // TODO: Replace with a facade
        return parents;
    }
}
