package net.glowstone.shiny.event;

import org.junit.Test;
import org.spongepowered.api.event.state.PostInitializationEvent;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Subscribe;

/**
 * Tests for an {@link EventManager} implementation.
 */
public class EventManagerTest {

    private final EventManager events = new ShinyEventManager();

    @Test
    public void whatup() {
        Object x = new Object() {
            @Subscribe
            public void catchAll(Event evt) {
                System.out.println("all: " + evt);
            }

            @Subscribe
            public void idkLol(Stuff evt) {
                System.out.println("idk: " + evt);
            }

            @Subscribe
            public void other(PostInitializationEvent evt) {
                System.out.println("other: " + evt);
            }
        };

        events.register(x, x);
        events.post(new Stuff());
        events.post(new BaseEvent());
    }

    private class Stuff extends BaseEvent implements Event {
        private Stuff() {
            super();
        }
    }
}
