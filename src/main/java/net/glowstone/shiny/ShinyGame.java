package net.glowstone.shiny;

import com.google.common.base.Optional;
import net.glowstone.shiny.event.ShinyEventManager;
import net.glowstone.shiny.plugin.ShinyPluginManager;
import net.glowstone.shiny.util.ConsoleManager;
import net.glowstone.shiny.util.Unsupported;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Platform;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.command.SimpleCommandService;
import org.spongepowered.api.service.scheduler.Scheduler;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.world.World;

import java.io.File;
import java.util.Collection;
import java.util.UUID;

/**
 * Implementation of {@link Game}.
 */
public class ShinyGame implements Game {

    public static final Logger logger = ConsoleManager.getLogger();

    private static final String API_VERSION;
    private static final String IMPL_VERSION;

    static {
        Package pkg = ShinyGame.class.getPackage();
        String apiVersion = pkg.getSpecificationVersion();
        API_VERSION = (apiVersion == null) ? "unknown" : apiVersion;
        String implVersion = pkg.getImplementationVersion();
        IMPL_VERSION = (implVersion == null) ? "unknown" : implVersion;
    }

    private final ShinyPluginManager pluginManager = new ShinyPluginManager(this);
    private final ShinyEventManager eventManager = new ShinyEventManager();
    private final ShinyGameRegistry registry = new ShinyGameRegistry();
    private final SimpleServiceManager services = new SimpleServiceManager(pluginManager);
    private final SimpleCommandService commands = new SimpleCommandService(pluginManager);

    public ShinyGame() {
        logger.info("Glowstone " + IMPL_VERSION + " is starting...");
        logger.info("API version: " + API_VERSION);
        /*
         CONSTRUCTION,
         LOAD_COMPLETE,
         PRE_INITIALIZATION,
         INITIALIZATION,
         POST_INITIALIZATION,
         SERVER_ABOUT_TO_START,
         SERVER_STARTING,
         SERVER_STARTED,
         SERVER_STOPPING,
         SERVER_STOPPED
         */
        File directory = new File("plugins");
        pluginManager.loadPlugins(directory);
    }

    // platform information

    @Override
    public Platform getPlatform() {
        return Platform.SERVER;
    }

    @Override
    public String getAPIVersion() {
        return API_VERSION;
    }

    @Override
    public String getImplementationVersion() {
        return IMPL_VERSION;
    }

    // service access

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public ShinyEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public GameRegistry getRegistry() {
        return registry;
    }

    @Override
    public ServiceManager getServiceManager() {
        return services;
    }

    @Override
    public Scheduler getScheduler() {
        throw Unsupported.missing();
    }

    @Override
    public CommandService getCommandDispatcher() {
        return commands;
    }

    // worlds

    @Override
    public Collection<World> getWorlds() {
        throw Unsupported.missing();
    }

    @Override
    public World getWorld(UUID uniqueId) {
        throw Unsupported.missing();
    }

    @Override
    public World getWorld(String worldName) {
        throw Unsupported.missing();
    }

    // players

    @Override
    public Collection<Player> getOnlinePlayers() {
        throw Unsupported.missing();
    }

    @Override
    public int getMaxPlayers() {
        throw Unsupported.missing();
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        throw Unsupported.missing();
    }

    @Override
    public Optional<Player> getPlayer(String name) {
        throw Unsupported.missing();
    }

    @Override
    public void broadcastMessage(Message<?> message) {
        logger.info("{}", message);
    }
}
