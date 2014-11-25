package spongetest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

import javax.inject.Inject;

@Plugin(id="spongetest", name="Sponge Test")
public class SpongeTestPlugin {
    public final Game game;
    public final PluginContainer container;

    public Logger factory_logger;
    public Logger event_logger;
    public File main_config_dir;
    public File rec_config_file;
    public File rec_config_dir;

    @Inject
    public SpongeTestPlugin(Game game, PluginContainer container) {
        this.game = game;
        this.container = container;

        factory_logger = LoggerFactory.getLogger("spongetest");
        factory_logger.info("SpongeTestPlugin constructed");
        factory_logger.info("game = " + game);
        factory_logger.info("container = " + container.getId());
    }

    @Subscribe
    public void onPreInit(PreInitializationEvent e) {
        event_logger = e.getPluginLog();
        main_config_dir = e.getConfigurationDirectory();
        rec_config_file = e.getRecommendedConfigurationFile();
        rec_config_dir = e.getRecommendedConfigurationDirectory();

        try {
            event_logger.info("Main Config Directory: " + main_config_dir.getCanonicalPath());
        } catch(Throwable t) {
            event_logger.info("Display Main Config Directory Failed", t);
        }

        try {
            event_logger.info("Recommended Config File: " + rec_config_file.getCanonicalPath());
        } catch(Throwable t) {
            event_logger.info("Display Recommended Config File Failed", t);
        }

        try {
            event_logger.info("Recommended Config Directory: " + rec_config_dir.getCanonicalPath());
        } catch(Throwable t) {
            event_logger.info("Display Recommended Config Directory Failed", t);
        }
    }
}
