package net.glowstone.inventory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Tracker for the item-drag operations a player is performing.
 */
public final class DragTracker {

    private final List<Integer> slots = new LinkedList<>();
    private boolean active;
    private boolean right;

    /**
     * Start tracking a drag operation if possible.
     * @param right True if the drag is a right-click drag.
     * @return True if the drag start was accepted.
     */
    public boolean start(boolean right) {
        if (this.active) {
            return false;
        } else {
            active = true;
            this.right = right;
            return true;
        }
    }

    /**
     * Add a slot to an in-progress drag operation if possible.
     * @param right True if the drag is a right-click drag.
     * @return True if the slot was accepted.
     */
    public boolean addSlot(boolean right, int slot) {
        if (!active || right != this.right) {
            return false;
        } else if (slots.contains(slot)) {
            return false;
        } else {
            slots.add(slot);
            return true;
        }
    }

    /**
     * Finish an in-progress drag operation if possible.
     * @param right True if the drag is a right-click drag.
     * @return The list of slots involved in the drag, or null on failure.
     */
    public List<Integer> finish(boolean right) {
        if (!active || right != this.right) {
            return null;
        } else if (slots.size() == 0) {
            return null;
        } else {
            List<Integer> result = new ArrayList<>(slots);
            reset();
            return result;
        }
    }

    /**
     * Reset back to the default state (no drag in progress).
     */
    public void reset() {
        slots.clear();
        active = false;
    }

}
