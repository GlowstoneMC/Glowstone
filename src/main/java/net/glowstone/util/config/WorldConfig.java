package net.glowstone.util.config;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.util.DynamicallyTypedMapWithDoubles;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Utilities for handling the server configuration files.
 */
public final class WorldConfig implements DynamicallyTypedMapWithDoubles<WorldConfig.Key> {

    /**
     * The directory configurations are stored in.
     */
    @Getter
    private final File directory;

    /**
     * The main configuration file.
     */
    private final File configFile;

    /**
     * The actual configuration data.
     */
    @Getter
    private final YamlConfiguration config = new YamlConfiguration();

    /**
     * The cache for config settings within a map.
     */
    private final Map<Key, Object> cache = new HashMap<>();

    /**
     * Initialize a new ServerConfig and associated settings.
     *
     * @param directory  The config directory, or null for default.
     * @param configFile The config file, or null for default.
     */
    public WorldConfig(File directory, File configFile) {
        checkNotNull(directory);
        checkNotNull(configFile);
        checkNotNull(cache);

        this.directory = directory;
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

    @Override
    public String getString(Key key) {
        if (cache.containsKey(key)) {
            return cache.get(key).toString();
        }
        String string = config.getString(key.path, key.def.toString());
        cache.put(key, string);
        return string;
    }

    @Override
    public int getInt(Key key) {
        if (cache.containsKey(key)) {
            return (Integer) cache.get(key);
        }
        int integer = config.getInt(key.path, (Integer) key.def);
        cache.put(key, integer);
        return integer;
    }

    @Override
    public double getDouble(Key key) {
        if (cache.containsKey(key)) {
            return (Double) cache.get(key);
        }
        double doub = config.getDouble(key.path, (Double) key.def);
        cache.put(key, doub);
        return doub;
    }

    @Override
    public float getFloat(Key key) {
        return (float) getDouble(key);
    }

    @Override
    public boolean getBoolean(Key key) {
        if (cache.containsKey(key)) {
            return (Boolean) cache.get(key);
        }
        boolean bool = config.getBoolean(key.path, (Boolean) key.def);
        cache.put(key, bool);
        return bool;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Load and internals

    /**
     * Loads the configuration from disk if it exists. Creates it if it doesn't exist, creating the
     * folder if necessary. Completes and saves the configuration if it's incomplete.
     */
    public void load() {
        boolean changed = false;

        // create default file if needed
        if (!configFile.exists()) {
            GlowServer.logger.info("Creating default world config: " + configFile);

            // create config directory
            if (!directory.isDirectory() && !directory.mkdirs()) {
                GlowServer.logger.severe("Cannot create directory: " + directory);
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
            GlowServer.logger
                .log(Level.SEVERE, "Cannot load " + file + ": " + e.getCause().getClass(), e);
        }
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
        OVERWORLD_BIOME_HEIGHT_OFFSET("overworld.biome.height-offset", 0D),
        OVERWORLD_BIOME_HEIGHT_WEIGHT("overworld.biome.height-weight", 1D),
        OVERWORLD_BIOME_SCALE_OFFSET("overworld.biome.scale-offset", 0D),
        OVERWORLD_BIOME_SCALE_WEIGHT("overworld.biome.scale-weight", 1D),
        OVERWORLD_DENSITY_FILL_MODE("overworld.density.fill.mode", 0),
        OVERWORLD_DENSITY_FILL_SEA_MODE("overworld.density.fill.sea-mode", 0),
        OVERWORLD_DENSITY_FILL_OFFSET("overworld.density.fill.offset", 0D),
        // Overworld biome heights
        BIOME_HEIGHT_DEFAULT("overworld.biome.height.default", 0.1),
        BIOME_SCALE_DEFAULT("overworld.biome.scale.default", 0.2),
        BIOME_HEIGHT_FLAT_SHORE("overworld.biome.height.flat-shore", 0D),
        BIOME_SCALE_FLAT_SHORE("overworld.biome.scale.flat-shore", 0.025),
        BIOME_HEIGHT_HIGH_PLATEAU("overworld.biome.height.high-plateau", 1.5),
        BIOME_SCALE_HIGH_PLATEAU("overworld.biome.scale.high-plateau", 0.025),
        BIOME_HEIGHT_FLATLANDS("overworld.biome.height.flatlands", 0.125),
        BIOME_SCALE_FLATLANDS("overworld.biome.scale.flatlands", 0.05),
        BIOME_HEIGHT_SWAMPLAND("overworld.biome.height.swampland", -0.2),
        BIOME_SCALE_SWAMPLAND("overworld.biome.scale.swampland", 0.1),
        BIOME_HEIGHT_MID_PLAINS("overworld.biome.height.mid-plains", 0.2),
        BIOME_SCALE_MID_PLAINS("overworld.biome.scale.mid-plains", 0.2),
        BIOME_HEIGHT_FLATLANDS_HILLS("overworld.biome.height.flatlands-hills", 0.275),
        BIOME_SCALE_FLATLANDS_HILLS("overworld.biome.scale.flatlands-hills", 0.25),
        BIOME_HEIGHT_SWAMPLAND_HILLS("overworld.biome.height.swampland-hills", -0.1),
        BIOME_SCALE_SWAMPLAND_HILLS("overworld.biome.scale.swampland-hills", 0.3),
        BIOME_HEIGHT_LOW_HILLS("overworld.biome.height.low-hills", 0.2),
        BIOME_SCALE_LOW_HILLS("overworld.biome.scale.low-hills", 0.3),
        BIOME_HEIGHT_HILLS("overworld.biome.height.hills", 0.45),
        BIOME_SCALE_HILLS("overworld.biome.scale.hills", 0.3),
        BIOME_HEIGHT_MID_HILLS2("overworld.biome.height.mid-hills2", 0.1),
        BIOME_SCALE_MID_HILLS2("overworld.biome.scale.mid-hills2", 0.4),
        BIOME_HEIGHT_DEFAULT_HILLS("overworld.biome.height.default-hills", 0.2),
        BIOME_SCALE_DEFAULT_HILLS("overworld.biome.scale.default-hills", 0.4),
        BIOME_HEIGHT_MID_HILLS("overworld.biome.height.mid-hills", 0.3),
        BIOME_SCALE_MID_HILLS("overworld.biome.scale.mid-hills", 0.4),
        BIOME_HEIGHT_BIG_HILLS("overworld.biome.height.big-hills", 0.525),
        BIOME_SCALE_BIG_HILLS("overworld.biome.scale.big-hills", 0.55),
        BIOME_HEIGHT_BIG_HILLS2("overworld.biome.height.big-hills2", 0.55),
        BIOME_SCALE_BIG_HILLS2("overworld.biome.scale.big-hills2", 0.5),
        BIOME_HEIGHT_EXTREME_HILLS("overworld.biome.height.extreme-hills", 1D),
        BIOME_SCALE_EXTREME_HILLS("overworld.biome.scale.extreme-hills", 0.5),
        BIOME_HEIGHT_ROCKY_SHORE("overworld.biome.height.rocky-shore", 0.1),
        BIOME_SCALE_ROCKY_SHORE("overworld.biome.scale.rocky-shore", 0.8),
        BIOME_HEIGHT_LOW_SPIKES("overworld.biome.height.low-spikes", 0.4125),
        BIOME_SCALE_LOW_SPIKES("overworld.biome.scale.low-spikes", 1.325),
        BIOME_HEIGHT_HIGH_SPIKES("overworld.biome.height.high-spikes", 1.1),
        BIOME_SCALE_HIGH_SPIKES("overworld.biome.scale.high-spikes", 1.3125),
        BIOME_HEIGHT_RIVER("overworld.biome.height.river", -0.5),
        BIOME_SCALE_RIVER("overworld.biome.scale.river", 0D),
        BIOME_HEIGHT_OCEAN("overworld.biome.height.ocean", -1D),
        BIOME_SCALE_OCEAN("overworld.biome.scale.ocean", 0.1),
        BIOME_HEIGHT_DEEP_OCEAN("overworld.biome.height.deep-ocean", -1.8),
        BIOME_SCALE_DEEP_OCEAN("overworld.biome.scale.deep-ocean", 0.1),
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
