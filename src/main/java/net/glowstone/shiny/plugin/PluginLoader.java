package net.glowstone.shiny.plugin;

import net.glowstone.shiny.Shiny;
import net.glowstone.shiny.ShinyGame;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Simple plugin loader for sets of jars.
 */
final class PluginLoader {

    private final ShinyGame game;

    public PluginLoader(ShinyGame game) {
        this.game = game;
    }

    /**
     * Load a list of SpongeAPI plugins
     *
     * @param urls URLs of potential plugins jars to load. This list is mutated, on return non-Sponge plugins
     *             are removed.
     * @return Loaded plugin containers
     */
    public List<PluginContainer> loadPlugins(List<URL> urls) {
        URLClassLoader root = new URLClassLoader(urls.toArray(new URL[urls.size()]), PluginLoader.class.getClassLoader());
        List<PluginContainer> result = new ArrayList<>(urls.size());

        Iterator<URL> iterator = urls.iterator();
        while (iterator.hasNext()) {
            URL url = iterator.next();

            if (loadJar(result, root, url))
                iterator.remove();
        }

        return result;
    }

    private boolean loadJar(List<PluginContainer> result, URLClassLoader root, URL url) {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, root);
        boolean hasPlugin = false;

        try (InputStream fileIn = url.openStream();
             ZipInputStream zipIn = new ZipInputStream(fileIn)
        ) {
            ZipEntry entryIn;
            while ((entryIn = zipIn.getNextEntry()) != null) {
                String name = entryIn.getName();
                if (name.endsWith(".class") && !entryIn.isDirectory()) {
                    name = name.substring(0, name.length() - 6).replace("/", ".");

                    Class<?> clazz;
                    try {
                        clazz = classLoader.loadClass(name);
                    } catch (Throwable t) {
                        Shiny.instance.logger.error("Error loading " + url.getFile() + "/" + name, t);
                        continue;
                    }

                    PluginContainer container = fromClass(clazz);

                    if (container != null) {
                        result.add(container);
                        hasPlugin = true;
                    }
                }
            }
        } catch (IOException ex) {
            Shiny.instance.logger.error("Error reading " + url, ex);
        }

        return hasPlugin;
    }

    private PluginContainer fromClass(Class<?> clazz) {
        Plugin annotation = clazz.getAnnotation(Plugin.class);
        if (annotation != null) {
            try {
                ShinyPluginContainer container = new ShinyPluginContainer(clazz);
                return container;
            } catch (Throwable t) {
                Shiny.instance.logger.error("Error initializing " + annotation.id() + " (" + clazz + ")", t);
            }
        }
        return null;
    }

}
