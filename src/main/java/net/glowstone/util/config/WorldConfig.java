package net.glowstone.util.config;

import net.glowstone.GlowServer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for handling the server configuration files.
 */
public final class WorldConfig {

    /**
     * The directory configurations are stored in.
     */
    private final File configDir;

    /**
     * The main configuration file.
     */
    private final File configFile;

    /**
     * The actual configuration data.
     */
    private final YamlConfiguration config = new YamlConfiguration();

    /**
     * The cache for config settings within a map.
     */
    private final Map<Key, Object> cache = new HashMap<>();

    /**
     * Initialize a new ServerConfig and associated settings.
     *
     * @param configDir  The config directory, or null for default.
     * @param configFile The config file, or null for default.
     */
    public WorldConfig(File configDir, File configFile) {
        checkNotNull(configDir);
        checkNotNull(configFile);
        checkNotNull(cache);

        this.configDir = configDir;
        this.configFile = configFile;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Modification

    /**
     * Save the configuration back to file.
     */
    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to write config: " + configFile, e);
        }
    }

    /**
     * Change a configuration value at runtime.
     *
     * @param key   the config key to write the value to
     * @param value value to write to config key
     * @see #save()
     */
    public void set(Key key, Object value) {
        cache.replace(key, value);
        config.set(key.path, value);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Value getters

    public String getString(Key key) {
        if (cache.containsKey(key)) {
            return cache.get(key).toString();
        }
        String string = config.getString(key.path, key.def.toString());
        cache.put(key, string);
        return string;
    }

    public int getInt(Key key) {
        if (cache.containsKey(key)) {
            return (Integer) cache.get(key);
        }
        int integer = config.getInt(key.path, (Integer) key.def);
        cache.put(key, integer);
        return integer;
    }

    public double getDouble(Key key) {
        if (cache.containsKey(key)) {
            return (Double) cache.get(key);
        }
        double doub = config.getDouble(key.path, (Double) key.def);
        cache.put(key, doub);
        return doub;
    }

    public boolean getBoolean(Key key) {
        if (cache.containsKey(key)) {
            return (Boolean) cache.get(key);
        }
        boolean bool = config.getBoolean(key.path, (Boolean) key.def);
        cache.put(key, bool);
        return bool;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy stuff

    public File getDirectory() {
        return configDir;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Load and internals

    public void load() {
        boolean changed = false;

        // create default file if needed
        if (!configFile.exists()) {
            GlowServer.logger.info("Creating default config: " + configFile);

            // create config directory
            if (!configDir.isDirectory() && !configDir.mkdirs()) {
                GlowServer.logger.severe("Cannot create directory: " + configDir);
                return;
            }

            // load default config
            for (Key key : Key.values()) {
                config.set(key.path, key.def);
            }

            changed = true;
        } else {
            // load config
            try {
                config.load(configFile);
            } catch (IOException e) {
                GlowServer.logger.log(Level.SEVERE, "Failed to read config: " + configFile, e);
            } catch (InvalidConfigurationException e) {
                report(configFile, e);
            }

            // add missing keys to the current config
            for (Key key : Key.values()) {
                if (!config.contains(key.path)) {
                    config.set(key.path, key.def);
                    changed = true;
                }
            }
        }

        if (changed) {
            save();
        }
    }

    private void report(File file, InvalidConfigurationException e) {
        if (e.getCause() instanceof YAMLException) {
            GlowServer.logger.severe("Config file " + file + " isn't valid! " + e.getCause());
        } else if (e.getCause() == null || e.getCause() instanceof ClassCastException) {
            GlowServer.logger.severe("Config file " + file + " isn't valid!");
        } else {
            GlowServer.logger.log(Level.SEVERE, "Cannot load " + file + ": " + e.getCause().getClass(), e);
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    /**
     * An enum containing configuration keys used by the server.
     */
    public enum Key {
        // World
        SEA_LEVEL("general.sea_level", 64),
        // Overworld generator
        OVERWORLD_COORDINATE_SCALE("overworld.coordinate-scale", 684.412),
        OVERWORLD_HEIGHT_SCALE("overworld.height.scale", 684.412),
        OVERWORLD_HEIGHT_NOISE_SCALE_X("overworld.height.noise-scale.x", 200D),
        OVERWORLD_HEIGHT_NOISE_SCALE_Z("overworld.height.noise-scale.z", 200D),
        OVERWORLD_DETAIL_NOISE_SCALE_X("overworld.detail.noise-scale.x", 80D),
        OVERWORLD_DETAIL_NOISE_SCALE_Y("overworld.detail.noise-scale.y", 160D),
        OVERWORLD_DETAIL_NOISE_SCALE_Z("overworld.detail.noise-scale.z", 80D),
        OVERWORLD_SURFACE_SCALE("overworld.surface-scale", 0.0625),
        OVERWORLD_BASE_SIZE("overworld.base-size", 8.5),
        OVERWORLD_STRETCH_Y("overworld.stretch-y", 12D),
        OVERWORLD_BIOME_HEIGHT_OFFSET("overworld.biome.height.offset", 0D),
        OVERWORLD_BIOME_HEIGHT_WEIGHT("overworld.biome.height.weight", 1D),
        OVERWORLD_BIOME_SCALE_OFFSET("overworld.biome.scale.offset", 0D),
        OVERWORLD_BIOME_SCALE_WEIGHT("overworld.biome.scale.weight", 1D),
        // Nether generator
        NETHER_COORDINATE_SCALE("nether.coordinate-scale", 684.412),
        NETHER_HEIGHT_SCALE("nether.height.scale", 2053.236),
        NETHER_HEIGHT_NOISE_SCALE_X("nether.height.noise-scale.x", 100D),
        NETHER_HEIGHT_NOISE_SCALE_Z("nether.height.noise-scale.z", 100D),
        NETHER_DETAIL_NOISE_SCALE_X("nether.detail.noise-scale.x", 80D),
        NETHER_DETAIL_NOISE_SCALE_Y("nether.detail.noise-scale.y", 60D),
        NETHER_DETAIL_NOISE_SCALE_Z("nether.detail.noise-scale.z", 80D),
        NETHER_SURFACE_SCALE("nether.surface-scale", 0.0625),
        // The End generator
        END_COORDINATE_SCALE("end.coordinate-scale", 684.412),
        END_HEIGHT_SCALE("end.height.scale", 1368.824),
        END_DETAIL_NOISE_SCALE_X("end.detail.noise-scale.x", 80D),
        END_DETAIL_NOISE_SCALE_Y("end.detail.noise-scale.y", 160D),
        END_DETAIL_NOISE_SCALE_Z("end.detail.noise-scale.z", 80D),
        ;

        private final String path;
        private final Object def;

        Key(String path, Object def) {
            this.path = path;
            this.def = def;
        }

        @Override
        public String toString() {
            return name() + "(" + path + ", " + def + ")";
        }
    }
}
