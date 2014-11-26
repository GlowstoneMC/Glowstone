package net.glowstone.shiny.event;

import org.spongepowered.api.event.*;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Implementation of {@link EventManager}.
 */
public class ShinyEventManager implements EventManager {

    private final Map<Order, List<EventRegistration>> registrations = new EnumMap<>(Order.class);

    public ShinyEventManager() {
        for (Order order : Order.values()) {
            registrations.put(order, new LinkedList<EventRegistration>());
        }
    }

    @Override
    public void register(Object obj) {
        for (Method method : obj.getClass().getMethods()) {
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            if (annotation != null && method.getParameterTypes().length == 1) {
                EventRegistration reg = new EventRegistration(obj, method, annotation.ignoreCancelled());
                registrations.get(annotation.order()).add(reg);
            }
        }
    }

    @Override
    public void unregister(Object obj) {
        for (List<EventRegistration> list : registrations.values()) {
            Iterator<EventRegistration> iter = list.iterator();
            while (iter.hasNext()) {
                if (iter.next().getObject() == obj) {
                    iter.remove();
                }
            }
        }
    }

    @Override
    public boolean call(Event event) {
        for (List<EventRegistration> list : registrations.values()) {
            for (EventRegistration reg : list) {
                reg.call(event);
            }
        }
        return !(event instanceof Cancellable) || ((Cancellable) event).isCancelled();
    }

    public void callSpecial(Object target, Event event) {
        for (List<EventRegistration> list : registrations.values()) {
            for (EventRegistration reg : list) {
                if (reg.getObject() == target) {
                    reg.call(event);
                }
            }
        }
    }
}
