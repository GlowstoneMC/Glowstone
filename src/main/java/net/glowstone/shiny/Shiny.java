package net.glowstone.shiny;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.glowstone.shiny.event.GraniteEventFactory;
import net.glowstone.shiny.guice.ShinyGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.event.state.ConstructionEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.StateEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

public class Shiny {

    public static final Shiny instance = new Shiny();

    public static Logger getLogger() {
        return instance.logger;
    }

    public Injector getInjector() {
        return injector;
    }

    private Injector injector;
    public Logger logger;

    private ShinyGame game;

    public Collection<URL> load(File[] files) {
        Collection<URL> loadedPluginURLs = null;

        try {
            logger = LoggerFactory.getLogger("Shiny");
            injector = Guice.createInjector(new ShinyGuiceModule());

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

            getLogger().info("Loading Shiny...");
            this.game = injector.getInstance(ShinyGame.class);

            getLogger().info("Glowstone " + this.game.getImplementationVersion() + " is starting...");
            getLogger().info("SpongeAPI version: " + this.game.getApiVersion());

            getLogger().info("Loading plugins...");
            loadedPluginURLs = this.game.getPluginManager().loadPlugins(files);
            postState(ConstructionEvent.class);
            getLogger().info("Initializing " + loadedPluginURLs.size() + " SpongeAPI plugins...");
            postState(PreInitializationEvent.class);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        return loadedPluginURLs;
    }

    public ShinyGame getGame() {
        return this.game;
    }

    public void postState(Class<? extends StateEvent> type) {
        this.game.getEventManager().post(GraniteEventFactory.createStateEvent(type, this.game));
    }

    public File getPluginsDirectory() {
        return new File("plugins"); // TODO
    }

    public File getConfigDirectory() {
        return new File("config"); // TODO
    }

}
