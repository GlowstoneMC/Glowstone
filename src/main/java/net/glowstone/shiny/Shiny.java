package net.glowstone.shiny;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.glowstone.shiny.util.ConsoleManager;
import org.slf4j.Logger;

import java.io.IOException;

public class Shiny {

    public static final Shiny instance = new Shiny();

    private Injector injector = Guice.createInjector(new ShinyGuiceModule());
    public final Logger logger = ConsoleManager.getLogger();

    private ShinyGame game;

    public static Injector getInjector() {
        return instance.injector;
    }

    public void load() {
        System.out.println("LOAD2");
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
