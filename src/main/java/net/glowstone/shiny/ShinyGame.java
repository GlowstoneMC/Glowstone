package net.glowstone.shiny;

import com.google.common.base.Optional;
import net.glowstone.shiny.event.ShinyEventManager;
import net.glowstone.shiny.plugin.ShinyPluginManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.scheduler.Scheduler;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.UUID;

/**
 * Implementation of {@link Game}.
 */
public class ShinyGame implements Game {

    private final ShinyPluginManager pluginManager = new ShinyPluginManager();
    private final ShinyEventManager eventManager = new ShinyEventManager();
    private final ShinyGameRegistry registry = new ShinyGameRegistry();

    @Override
    public Platform getPlatform() {
        return Platform.SERVER;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public GameRegistry getRegistry() {
        return registry;
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return null;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return null;
    }

    @Override
    public Collection<World> getWorlds() {
        return null;
    }

    @Override
    public World getWorld(UUID uniqueId) {
        return null;
    }

    @Override
    public World getWorld(String worldName) {
        return null;
    }

    @Override
    public String getAPIVersion() {
        return null;
    }

    @Override
    public String getImplementationVersion() {
        return null;
    }

    @Override
    public ServiceManager getServiceManager() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public CommandService getCommandDispatcher() {
        return null;
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        return null;
    }

    @Override
    public void broadcastMessage(Message<?> message) {

    }
}
