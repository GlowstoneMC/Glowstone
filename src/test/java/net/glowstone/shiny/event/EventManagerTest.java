package net.glowstone.shiny.event;

import org.junit.Test;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.SpongeEventHandler;
import org.spongepowered.api.event.state.PostInitializationEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;

/**
 * Tests for an {@link EventManager} implementation.
 */
public class EventManagerTest {

    private final EventManager events = new ShinyEventManager();

    @Test
    public void whatup() {
        Object x = new Object() {
            @SpongeEventHandler
            public void catchAll(Event evt) {
                System.out.println("all: " + evt);
            }

            @SpongeEventHandler
            public void idkLol(PreInitializationEvent evt) {
                System.out.println("idk: " + evt);
            }

            @SpongeEventHandler
            public void other(PostInitializationEvent evt) {
                System.out.println("other: " + evt);
            }
        };

        events.register(x);
        events.call(new Stuff(null));
        events.call(new BaseEvent(null));
    }

    private class Stuff extends BaseEvent implements PreInitializationEvent {
        private Stuff(Game game) {
            super(game);
        }
    }
}
