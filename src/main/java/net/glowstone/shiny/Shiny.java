package net.glowstone.shiny;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.glowstone.shiny.util.ConsoleManager;
import org.slf4j.Logger;

public class Shiny {

    public static final Shiny instance = new Shiny();

    private static final Injector injector = Guice.createInjector(new ShinyGuiceModule());
    public static final Logger logger = ConsoleManager.getLogger();

    private ShinyGame game;

    public static Injector getInjector() {
        return injector;
    }

    public void load() {
        try {
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

            logger.info("Loading Shiny...");
            this.game = injector.getInstance(ShinyGame.class);

            logger.info("Glowstone " + this.game.getImplementationVersion() + " is starting...");
            logger.info("API version: " + this.game.getApiVersion());

            logger.info("Loading plugins...");
            this.game.getPluginManager().loadPlugins();
            // TODO: postState
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
