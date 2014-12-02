package net.glowstone.shiny.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.glowstone.shiny.ShinyGame;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

/**
 * Guice injection module for plugin loading.
 */
class PluginModule extends AbstractModule {
    private final ShinyGame game;
    private final ShinyPluginContainer container;

    public PluginModule(ShinyGame game, ShinyPluginContainer container) {
        this.game = game;
        this.container = container;
    }

    @Override
    protected void configure() {
        bind(Game.class).toInstance(game);
        bind(PluginContainer.class).toInstance(container);
        bind(Logger.class).toInstance(game.getPluginManager().getLogger(container));

        namedFile("GeneralConfigDir", new File("config"));
        namedFile("PluginConfigFile", new File("config", container.getId() + ".cfg"));
        namedFile("PluginConfigDir", new File("config", container.getId()));
    }

    private void namedFile(String name, File file) {
        bind(File.class).annotatedWith(Names.named(name)).toInstance(file);
    }
}
