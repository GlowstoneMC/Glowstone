package net.glowstone.shiny.plugin;

import com.google.common.base.Optional;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.glowstone.shiny.Shiny;
import net.glowstone.shiny.ShinyGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Implementation of {@link PluginManager}.
 */

@Singleton
public class ShinyPluginManager implements PluginManager {

    private final ShinyGame game;
    private final PluginLoader loader;

    private final Map<String, PluginContainer> plugins = new HashMap<>();
    private final Map<Object, PluginContainer> instanceMap = new IdentityHashMap<>();

    @Inject
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
        return plugins.containsKey(id);
    }

    public void loadPlugins() throws IOException {
        File directory = new File("plugins"); // TODO: pass plugin directory
        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar"));
        if (files == null || files.length == 0) {
            return;
        }

        List<URL> urls = new ArrayList<>(files.length);
        for (File jar : files) {
            try {
                urls.add(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                Shiny.instance.logger.warn("Malformed URL: " + jar, e);
            }
        }

        Collection<PluginContainer> containers = loader.loadPlugins(urls);
        for (PluginContainer container : containers) {
            if (plugins.containsKey(container.getId())) {
                Shiny.instance.logger.warn("Skipped loading duplicate of \"" + container.getId() + "\"");
                continue;
            }
            plugins.put(container.getId(), container);
            instanceMap.put(container.getInstance(), container);
            game.getEventManager().register(container.getInstance(), container.getInstance());
        }

        for (URL url : urls) {
            Shiny.instance.logger.info("Non-SpongeAPI plugin: " + url); // TODO: pass to Bukkit
        }
    }
}
