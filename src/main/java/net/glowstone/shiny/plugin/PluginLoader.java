package net.glowstone.shiny.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.glowstone.shiny.ShinyGame;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Simple plugin loader for sets of jars.
 */
final class PluginLoader {

    private final ShinyGame game;
    private final List<URL> classpath = new LinkedList<>();

    public PluginLoader(ShinyGame game) {
        this.game = game;
    }

    public List<PluginContainer> loadPlugins(File[] jars) {
        List<URL> urls = new ArrayList<>(jars.length);
        for (File jar : jars) {
            try {
                urls.add(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                // skip this
            }
        }

        URLClassLoader root = new URLClassLoader(urls.toArray(new URL[urls.size()]), PluginLoader.class.getClassLoader());
        List<PluginContainer> result = new ArrayList<>(jars.length);

        for (URL url : urls) {
            loadJar(result, root, url);
        }

        return result;
    }

    private void loadJar(List<PluginContainer> result, URLClassLoader root, URL url) {
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, root);

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
                        t.printStackTrace();
                        return;
                    }

                    Plugin annotation = clazz.getAnnotation(Plugin.class);
                    if (annotation != null) {
                        ShinyPluginContainer container = new ShinyPluginContainer(annotation);
                        Injector injector = Guice.createInjector(new PluginModule(container));
                        container.instance = injector.getInstance(clazz);
                        result.add(container);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private class PluginModule extends AbstractModule {
        private final ShinyPluginContainer container;

        public PluginModule(ShinyPluginContainer container) {
            this.container = container;
        }

        @Override
        protected void configure() {
            bind(Game.class).toInstance(game);
            bind(PluginContainer.class).toInstance(container);
        }
    }
}
