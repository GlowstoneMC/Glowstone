package net.glowstone.shiny.event;

import org.junit.Test;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PostInitializationEvent;

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

        events.register(x);
        events.call(new Stuff());
        events.call(new BaseEvent());
    }

    private class Stuff extends BaseEvent implements Event {
        private Stuff() {
            super();
        }
    }
}
