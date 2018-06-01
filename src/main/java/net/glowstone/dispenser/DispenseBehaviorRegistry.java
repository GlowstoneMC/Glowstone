package net.glowstone.dispenser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Material;

public class DispenseBehaviorRegistry {

    private final Map<Material, DispenseBehavior> dispenseBehaviorMap = new ConcurrentHashMap<>();

    public void putBehavior(Material material, DispenseBehavior behavior) {
        dispenseBehaviorMap.put(material, behavior);
    }

    public void resetBehavior(Material material) {
        dispenseBehaviorMap.remove(material);
    }

    /**
     * Returns the dispense behavior for the given item type.
     *
     * @param material the item type to look up
     * @return the dispense behavior
     */
    public DispenseBehavior getBehavior(Material material) {
        if (material == null) {
            return DefaultDispenseBehavior.INSTANCE;
        }

        DispenseBehavior behavior = dispenseBehaviorMap.get(material);
        if (behavior == null) {
            return DefaultDispenseBehavior.INSTANCE;
        }

        return behavior;
    }
}
