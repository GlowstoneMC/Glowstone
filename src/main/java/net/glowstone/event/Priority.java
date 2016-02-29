package net.glowstone.event;

import lombok.AllArgsConstructor;
import net.glowstone.interfaces.IHandlerList;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.RegisteredListener;
import org.spongepowered.api.event.Order;

public class Priority {

    /*
    PRE,
    AFTER_PRE,
    FIRST,
    EARLY,
    DEFAULT,
    LATE,
    LAST,
    BEFORE_POST,
    POST
    */

    public static final Priority SPONGE_PRE = new SpongePriority(Order.PRE);
    public static final Priority SPONGE_AFTER_PRE = new SpongePriority(Order.AFTER_PRE);

    public static final Priority BUKKIT_HIGHEST = new BukkitPriority(EventPriority.HIGHEST);
    public static final Priority SPONGE_FIRST = new SpongePriority(Order.FIRST);

    public static final Priority BUKKIT_HIGH = new BukkitPriority(EventPriority.HIGH);
    public static final Priority SPONGE_EARLY = new SpongePriority(Order.EARLY);

    public static final Priority BUKKIT_NORMAL = new BukkitPriority(EventPriority.NORMAL);
    public static final Priority SPONGE_DEFAULT = new SpongePriority(Order.DEFAULT);

    public static final Priority BUKKIT_LOW = new BukkitPriority(EventPriority.LOW);
    public static final Priority SPONGE_LATE = new SpongePriority(Order.LATE);

    public static final Priority BUKKIT_LOWEST = new BukkitPriority(EventPriority.LOWEST);
    public static final Priority SPONGE_LAST = new SpongePriority(Order.LAST);

    public static final Priority BUKKIT_MONITOR = new BukkitPriority(EventPriority.MONITOR);
    public static final Priority SPONGE_BEFORE_POST = new SpongePriority(Order.BEFORE_POST);
    public static final Priority SPONGE_POST = new SpongePriority(Order.POST);

    public static final Priority[] PRIORITIES = new Priority[]{
            SPONGE_PRE,
            SPONGE_AFTER_PRE,

            BUKKIT_HIGHEST,
            SPONGE_FIRST,

            BUKKIT_HIGH,
            SPONGE_EARLY,

            BUKKIT_NORMAL,
            SPONGE_DEFAULT,

            BUKKIT_LOW,
            SPONGE_LATE,

            BUKKIT_LOWEST,
            SPONGE_LAST,

            BUKKIT_MONITOR,
            SPONGE_BEFORE_POST,
            SPONGE_POST
    };

    public static Priority getFromBukkit(EventPriority priority) {
        switch (priority) {
            case HIGHEST:
                return BUKKIT_HIGHEST;
            case HIGH:
                return BUKKIT_HIGH;
            case NORMAL:
                return BUKKIT_NORMAL;
            case LOW:
                return BUKKIT_LOW;
            case LOWEST:
                return BUKKIT_LOWEST;
            case MONITOR:
                return BUKKIT_MONITOR;
            default:
                throw new IllegalArgumentException("WTF?");
        }
    }

    public void callBukkit(EventRegister register, org.bukkit.event.Event event) { }

    public void callSponge(SpongeEventManager eventManager, org.spongepowered.api.event.Event event) { }

    @AllArgsConstructor
    private static class BukkitPriority extends Priority {

        private final EventPriority priority;

        @Override
        public void callBukkit(EventRegister register, org.bukkit.event.Event event) {
            for (RegisteredListener registration : ((IHandlerList) event.getHandlers()).getRegisteredListenersByPriority(priority)) {
                register.fireBukkitEvent(event, registration);
            }
        }
    }

    @AllArgsConstructor
    private static class SpongePriority extends Priority {

        private final Order order;

        @Override
        public void callSponge(SpongeEventManager eventManager, org.spongepowered.api.event.Event event) {
            eventManager.post(event, order);
        }
    }
}
