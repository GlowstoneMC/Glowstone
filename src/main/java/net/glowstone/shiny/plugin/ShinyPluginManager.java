package net.glowstone.shiny.plugin;

import com.google.common.base.Optional;
import com.google.common.io.PatternFilenameFilter;
import net.glowstone.shiny.ShinyGame;
import net.glowstone.shiny.event.ShinyPreInitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Implementation of {@link PluginManager}.
 */
public class ShinyPluginManager implements PluginManager {

    private final ShinyGame game;
    private final PluginLoader loader;

    private final Map<String, PluginContainer> plugins = new HashMap<>();
    private final Map<Object, PluginContainer> instanceMap = new IdentityHashMap<>();

    public ShinyPluginManager(ShinyGame game) {
        this.game = game;
        loader = new PluginLoader(game);
    }

    @Override
    public Optional<PluginContainer> fromInstance(Object instance) {
        return Optional.fromNullable(instanceMap.get(instance));
    }

    @Override
    public Optional<PluginContainer> getPlugin(String id) {
        return Optional.fromNullable(plugins.get(id));
    }

    @Override
    public Logger getLogger(PluginContainer plugin) {
        return LoggerFactory.getLogger(plugin.getName());
    }

    @Override
    public Collection<PluginContainer> getPlugins() {
        return plugins.values();
    }

    @Override
    public boolean isLoaded(String id) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void loadPlugins(File directory) {
        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar"));
        if (files == null || files.length == 0) {
            return;
        }

        Collection<PluginContainer> containers = loader.loadPlugins(files);
        for (PluginContainer container : containers) {
            if (plugins.containsKey(container.getId())) {
                ShinyGame.logger.warn("Skipped loading duplicate of \"" + container.getId() + "\"");
                continue;
            }
            plugins.put(container.getId(), container);
            instanceMap.put(container.getInstance(), container);
            game.getEventManager().register(container.getInstance(), container.getInstance());
            game.getEventManager().callSpecial(container.getInstance(), new ShinyPreInitEvent(game, container));
        }
    }
}
