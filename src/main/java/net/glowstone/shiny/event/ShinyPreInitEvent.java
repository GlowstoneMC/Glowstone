package net.glowstone.shiny.event;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;

/**
 * Implementation of {@link PreInitializationEvent}.
 */
public class ShinyPreInitEvent extends BaseEvent implements PreInitializationEvent {

    private final Logger logger;
    private final File configDir;
    private final File recConfigFile;
    private final File recConfigDir;
    private final Game game;

    public ShinyPreInitEvent(Game game, PluginContainer container) {
        this.game = game;
        logger = game.getPluginManager().getLogger(container);
        configDir = new File("config");
        recConfigFile = new File(configDir, container.getId() + ".cfg");
        recConfigDir = new File(configDir, container.getId());
    }

    @Override
    public Game getGame() {
        return game;
    }
}
