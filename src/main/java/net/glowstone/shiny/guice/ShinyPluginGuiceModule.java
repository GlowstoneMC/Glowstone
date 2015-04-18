package net.glowstone.shiny.guice;

// based on https://github.com/SpongePowered/SpongeVanilla/blob/master/src/main/java/org/spongepowered/granite/guice/GranitePluginGuiceModule.java

/*
 * This file is part of Granite, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <http://github.com/SpongePowered>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import net.glowstone.shiny.plugin.ShinyPluginContainer;
import java.io.File;
import javax.inject.Inject;

public class ShinyPluginGuiceModule extends AbstractModule {

    private final ShinyPluginContainer container;

    public ShinyPluginGuiceModule(ShinyPluginContainer container) {
        this.container = container;
    }

    @Override
    protected void configure() {
        DefaultConfig pluginConfigPrivate = new ConfigFileAnnotation(false);
        DefaultConfig pluginConfigShared = new ConfigFileAnnotation(true);
        ConfigDir pluginDirPrivate = new ConfigDirAnnotation(false);

        bind(PluginContainer.class).toInstance(this.container);
        bind(Logger.class).toInstance(LoggerFactory.getLogger(this.container.getId()));
        bind(File.class).annotatedWith(pluginDirPrivate)
                .toProvider(PluginConfigDirProvider.class); // plugin-private config directory (shared dir is in the global guice module)
        bind(File.class).annotatedWith(pluginConfigShared).toProvider(PluginSharedConfigFileProvider.class); // shared-directory config file
        bind(File.class).annotatedWith(pluginConfigPrivate).toProvider(PluginPrivateConfigFileProvider.class); // plugin-private directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(pluginConfigShared)
                .toProvider(PluginSharedHoconConfigProvider.class); // loader for shared-directory config file
        bind(new TypeLiteral<ConfigurationLoader<CommentedConfigurationNode>>() {
        }).annotatedWith(pluginConfigPrivate)
                .toProvider(PluginPrivateHoconConfigProvider.class); // loader for plugin-private directory config file
    }

    private static class PluginSharedConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final File root;

        @Inject
        private PluginSharedConfigFileProvider(PluginContainer container, @ConfigDir(sharedRoot = true) File sharedConfigDir) {
            this.container = container;
            this.root = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.root, this.container.getId() + ".conf");
        }
    }

    private static class PluginPrivateConfigFileProvider implements Provider<File> {

        private final PluginContainer container;
        private final File root;

        @Inject
        private PluginPrivateConfigFileProvider(PluginContainer container, @ConfigDir(sharedRoot = false) File sharedConfigDir) {
            this.container = container;
            this.root = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.root, this.container.getId() + ".conf");
        }
    }

    private static class PluginSharedHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final File configFile;

        @Inject
        private PluginSharedHoconConfigProvider(@DefaultConfig(sharedRoot = true) File configFile) {
            this.configFile = configFile;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return HoconConfigurationLoader.builder().setFile(this.configFile).build();
        }
    }

    private static class PluginPrivateHoconConfigProvider implements Provider<ConfigurationLoader<CommentedConfigurationNode>> {

        private final File configFile;

        @Inject
        private PluginPrivateHoconConfigProvider(@DefaultConfig(sharedRoot = false) File configFile) {
            this.configFile = configFile;
        }

        @Override
        public ConfigurationLoader<CommentedConfigurationNode> get() {
            return HoconConfigurationLoader.builder().setFile(this.configFile).build();
        }
    }

    private static class PluginConfigDirProvider implements Provider<File> {

        private final PluginContainer container;
        private final File sharedConfigDir;

        @Inject
        private PluginConfigDirProvider(PluginContainer container, @ConfigDir(sharedRoot = true) File sharedConfigDir) {
            this.container = container;
            this.sharedConfigDir = sharedConfigDir;
        }

        @Override
        public File get() {
            return new File(this.sharedConfigDir, this.container.getId() + "/");
        }
    }
}
