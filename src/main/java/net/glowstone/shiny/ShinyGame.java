package net.glowstone.shiny;

import com.google.common.base.Optional;
import net.glowstone.shiny.event.ShinyEventManager;
import net.glowstone.shiny.plugin.ShinyPluginManager;
import net.glowstone.shiny.util.ConsoleManager;
import net.glowstone.shiny.util.Unsupported;
import org.slf4j.Logger;
import org.spongepowered.api.*;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.command.SimpleCommandService;
import org.spongepowered.api.service.scheduler.AsynchronousScheduler;
import org.spongepowered.api.service.scheduler.Scheduler;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.text.Text;
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
    public Server getServer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getImplementationVersion() {
        return IMPL_VERSION;
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public SynchronousScheduler getSyncScheduler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AsynchronousScheduler getAsyncScheduler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommandService getCommandDispatcher() {
        return commands;
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }
}
