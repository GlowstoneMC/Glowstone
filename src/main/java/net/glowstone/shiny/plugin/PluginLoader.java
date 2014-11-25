package net.glowstone.shiny.plugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.glowstone.shiny.MixinTest;
import net.glowstone.shiny.ShinyGame;
import org.spongepowered.api.Game;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Simple test plugin loader for single jars.
 */
class PluginLoader {

    private final ShinyGame game;

    public PluginLoader(ShinyGame game) {
        this.game = game;
    }

    public PluginContainer loadPlugin(File jar) throws IOException {
        URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, MixinTest.class.getClassLoader());

        // build field name lists
        try (FileInputStream fileIn = new FileInputStream(jar);
             ZipInputStream zipIn = new ZipInputStream(fileIn)
        ) {
            ZipEntry entryIn;
            while ((entryIn = zipIn.getNextEntry()) != null) {
                String name = entryIn.getName();
                if (name.endsWith(".class") && !entryIn.isDirectory()) {
                    String className = name.substring(0, name.length() - 6).replace("/", ".");
                    Class<?> clazz;
                    try {
                        clazz = loader.loadClass(className);
                    } catch (ClassNotFoundException e) {
                        continue;
                    }

                    Plugin annotation = clazz.getAnnotation(Plugin.class);
                    if (annotation != null) {
                        ShinyPluginContainer container = new ShinyPluginContainer(annotation);
                        Injector injector = Guice.createInjector(new PluginModule(container));
                        Object plugin = container.instance = injector.getInstance(clazz);
                        game.getEventManager().register(plugin);
                        return container;
                    }
                }
            }
        }

        return null;
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
