package net.glowstone.event;

import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.plugin.GlowPluginManager;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.Plugin;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.plugin.PluginManager;

import java.util.logging.Level;

public class EventRegister {

    @Getter
    private SpongeEventManager eventManager;
    private GlowServer server;

    public EventRegister(GlowPluginManager pluginManager, GlowServer server) {
        this.eventManager = new SpongeEventManager((PluginManager) (Object) pluginManager, this);
        this.server = server;
    }

    public boolean callEvent(Object event) {

        boolean isSponge;
        Event spongeEvent = null;
        boolean isBukkit;
        org.bukkit.event.Event bukkitEvent = null;

        if (isSponge = event instanceof Event) {
            spongeEvent = (Event) event;
        }

        if (isBukkit = event instanceof org.bukkit.event.Event) {
            bukkitEvent = (org.bukkit.event.Event) event;
        }

        if (isBukkit && isSponge) { //both
            for (Priority priority : Priority.PRIORITIES) {
                priority.callSponge(eventManager, spongeEvent);
                priority.callBukkit(this, bukkitEvent);
            }
        } else {
            if (isSponge) {
                return eventManager.post0(spongeEvent);
            } else if (isBukkit) {
                for (org.bukkit.plugin.RegisteredListener registration : bukkitEvent.getHandlers().getRegisteredListeners()) {
                    fireBukkitEvent(bukkitEvent, registration);
                }
            }
        }

        return event instanceof Cancellable && ((Cancellable) event).isCancelled();
    }

    void fireBukkitEvent(org.bukkit.event.Event event, org.bukkit.plugin.RegisteredListener registration) {
        if (!registration.getPlugin().isEnabled()) {
            return;
        }

        try {
            registration.callEvent(event);
        } catch (AuthorNagException ex) {
            Plugin plugin = registration.getPlugin();

            if (plugin.isNaggable()) {
                plugin.setNaggable(false);

                server.getLogger().log(Level.SEVERE, String.format(
                        "Nag author(s): '%s' of '%s' about the following: %s",
                        plugin.getDescription().getAuthors(),
                        plugin.getDescription().getFullName(),
                        ex.getMessage()
                ));
            }
        } catch (Throwable ex) {
            server.getLogger().log(Level.SEVERE, "Could not pass event " + event.getEventName() + " to " + registration.getPlugin().getDescription().getFullName(), ex);
        }
    }
}
