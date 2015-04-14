package net.glowstone.shiny;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Shiny {

    public static final Shiny instance = new Shiny();

    private Injector injector;
    public Logger logger;

    private ShinyGame game;

    public void load() {
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

        logger.info("Loading Shiny...");
        this.game = injector.getInstance(ShinyGame.class);

        logger.info("Glowstone " + this.game.getImplementationVersion() + " is starting...");
        logger.info("SpongeAPI version: " + this.game.getApiVersion());

        logger.info("Loading plugins...");
        try {
            this.game.getPluginManager().loadPlugins();
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        // TODO: postState
    }
}
