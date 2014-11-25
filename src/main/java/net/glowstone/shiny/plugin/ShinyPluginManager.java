package net.glowstone.shiny.plugin;

import com.google.common.base.Optional;
import net.glowstone.shiny.ShinyGame;
import net.glowstone.shiny.event.ShinyPreInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

/**
 * Implementation of {@link PluginManager}.
 */
public class ShinyPluginManager implements PluginManager {

    private final ShinyGame game;
    private final HashMap<String, PluginContainer> plugins = new HashMap<>();
    private final PluginLoader loader;

    public ShinyPluginManager(ShinyGame game) {
        this.game = game;
        loader = new PluginLoader(game);
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.fromNullable(plugins.get(id));
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return LoggerFactory.getLogger("Plugin/" + plugin.getName());
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return plugins.values();
    }

    public void loadPlugin(File jar) {
        try {
            PluginContainer container = loader.loadPlugin(jar);
            System.out.println("!! Loaded " + container.getId());
            plugins.put(container.getId(), container);

            System.out.println("=== PreInit ===");
            game.getEventManager().call(new ShinyPreInitEvent(game, container));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
