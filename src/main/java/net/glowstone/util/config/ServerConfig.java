package net.glowstone.util.config;

import net.glowstone.GlowServer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utilities for handling the server configuration files.
 */
public final class ServerConfig {

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
     * Extra configuration files (help, permissions, commands).
     */
    private final Map<String, YamlConfiguration> extraConfig = new HashMap<>();

    /**
     * Parameters with which the server is ran.
     */
    private final Map<Key, Object> parameters;

    /**
     * Initialize a new ServerConfig and associated settings.
     *
     * @param configDir  The config directory, or null for default.
     * @param configFile The config file, or null for default.
     * @param parameters The command-line parameters used as overrides.
     */
    public ServerConfig(File configDir, File configFile, Map<Key, Object> parameters) {
        checkNotNull(configDir);
        checkNotNull(configFile);
        checkNotNull(parameters);

        this.configDir = configDir;
        this.configFile = configFile;
        this.parameters = parameters;

        config.options().indent(4).copyHeader(true).header(
                "glowstone.yml is the main configuration file for a Glowstone server\n" +
                        "It contains everything from server.properties and bukkit.yml in a\n" +
                        "normal CraftBukkit installation.\n\n" +
                        "For help, join us on Discord: https://discord.gg/TFJqhsC");
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
     * @see ServerConfig#save()
     */
    public void set(Key key, Object value) {
        parameters.replace(key, value);
        config.set(key.path, value);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Value getters

    public String getString(Key key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key).toString();
        }
        String string = config.getString(key.path, key.def.toString());
        parameters.put(key, string);
        return string;
    }

    public int getInt(Key key) {
        if (parameters.containsKey(key)) {
            return (Integer) parameters.get(key);
        }
        int integer = config.getInt(key.path, (Integer) key.def);
        parameters.put(key, integer);
        return config.getInt(key.path, (Integer) key.def);
    }

    public boolean getBoolean(Key key) {
        if (parameters.containsKey(key)) {
            return (Boolean) parameters.get(key);
        }
        boolean bool = config.getBoolean(key.path, (Boolean) key.def);
        parameters.put(key, bool);
        return bool;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy stuff

    public ConfigurationSection getConfigFile(Key key) {
        String filename = getString(key);
        if (extraConfig.containsKey(filename)) {
            return extraConfig.get(filename);
        }

        YamlConfiguration conf = new YamlConfiguration();
        File file = getFile(filename), migrateFrom = new File(key.def.toString());

        // create file if it doesn't exist
        if (!file.exists()) {
            if (migrateFrom.exists()) {
                FileUtil.copy(migrateFrom, file);
            } else {
                copyDefaults(key.def.toString(), file);
            }
        }

        // read in config
        try {
            conf.load(file);
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to read config: " + file, e);
        } catch (InvalidConfigurationException e) {
            report(file, e);
        }

        extraConfig.put(filename, conf);
        return conf;
    }

    public ConfigurationSection getWorlds() {
        return config.getConfigurationSection("worlds");
    }

    public File getDirectory() {
        return configDir;
    }

    public File getFile(String filename) {
        return new File(configDir, filename);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Load and internals

    public void load() {
        // load extra config files again next time they're needed
        extraConfig.clear();

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

            // attempt to migrate
            if (migrate()) {
                GlowServer.logger.info("Migrated configuration from previous installation");
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

    private void copyDefaults(String source, File dest) {
        URL resource = getClass().getClassLoader().getResource("defaults/" + source);
        if (resource == null) {
            GlowServer.logger.warning("Could not find default " + source + " on classpath");
            return;
        }

        try (final InputStream in = resource.openStream();
             final OutputStream out = new FileOutputStream(dest)) {
            byte[] buf = new byte[2048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            GlowServer.logger.log(Level.WARNING, "Could not save default config: " + dest, e);
            return;
        }

        GlowServer.logger.info("Created default config: " + dest);
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

    private boolean migrate() {
        boolean migrateStatus = false;

        File bukkitYml = new File("bukkit.yml");
        if (bukkitYml.exists()) {
            YamlConfiguration bukkit = new YamlConfiguration();
            try {
                bukkit.load(bukkitYml);
            } catch (InvalidConfigurationException e) {
                report(bukkitYml, e);
            } catch (IOException e) {
                GlowServer.logger.log(Level.WARNING, "Could not migrate from " + bukkitYml, e);
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.BUKKIT && bukkit.contains(key.migratePath)) {
                    config.set(key.path, bukkit.get(key.migratePath));
                    migrateStatus = true;
                }
            }

            config.set("aliases", bukkit.get("aliases"));
            config.set("worlds", bukkit.get("worlds"));
        }

        File serverProps = new File("server.properties");
        if (serverProps.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(serverProps));
            } catch (IOException e) {
                GlowServer.logger.log(Level.WARNING, "Could not migrate from " + serverProps, e);
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.PROPS && props.containsKey(key.migratePath)) {
                    String value = props.getProperty(key.migratePath);
                    if (key.def instanceof Integer) {
                        try {
                            config.set(key.path, Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            GlowServer.logger.log(Level.WARNING, "Could not migrate " + key.migratePath + " from " + serverProps, e);
                            continue;
                        }
                    } else if (key.def instanceof Boolean) {
                        config.set(key.path, Boolean.parseBoolean(value));
                    } else {
                        config.set(key.path, value);
                    }
                    migrateStatus = true;
                }
            }
        }

        return migrateStatus;
    }

    /**
     * An enum containing configuration keys used by the server.
     */
    public enum Key {
        // server
        SERVER_IP("server.ip", "", Migrate.PROPS, "server-ip"),
        SERVER_PORT("server.port", 25565, Migrate.PROPS, "server-port"),
        SERVER_NAME("server.name", "Glowstone Server", Migrate.PROPS, "server-name"),
        LOG_FILE("server.log-file", "logs/log-%D.txt"),
        ONLINE_MODE("server.online-mode", true, Migrate.PROPS, "online-mode"),
        MAX_PLAYERS("server.max-players", 20, Migrate.PROPS, "max-players"),
        WHITELIST("server.whitelisted", false, Migrate.PROPS, "white-list"),
        MOTD("server.motd", "A Glowstone server", Migrate.PROPS, "motd"),
        SHUTDOWN_MESSAGE("server.shutdown-message", "Server shutting down.", Migrate.BUKKIT, "settings.shutdown-message"),
        ALLOW_CLIENT_MODS("server.allow-client-mods", true),

        // console
        USE_JLINE("console.use-jline", true),
        CONSOLE_PROMPT("console.prompt", ">"),
        CONSOLE_DATE("console.date-format", "HH:mm:ss"),
        CONSOLE_LOG_DATE("console.log-date-format", "yyyy/MM/dd HH:mm:ss"),

        // game props
        GAMEMODE("game.gamemode", "SURVIVAL", Migrate.PROPS, "gamemode"),
        FORCE_GAMEMODE("game.gamemode-force", false, Migrate.PROPS, "force-gamemode"),
        DIFFICULTY("game.difficulty", "NORMAL", Migrate.PROPS, "difficulty"),
        HARDCORE("game.hardcore", false, Migrate.PROPS, "hardcore"),
        PVP_ENABLED("game.pvp", true, Migrate.PROPS, "pvp"),
        MAX_BUILD_HEIGHT("game.max-build-height", 256, Migrate.PROPS, "max-build-height"),
        ANNOUNCE_ACHIEVEMENTS("game.announce-achievements", true, Migrate.PROPS, "announce-player-achievements"),

        // server.properties keys
        ALLOW_FLIGHT("game.allow-flight", false, Migrate.PROPS, "allow-flight"),
        ENABLE_COMMAND_BLOCK("game.command-blocks", false, Migrate.PROPS, "enable-command-block"),
        //OP_PERMISSION_LEVEL(null, Migrate.PROPS, "op-permission-level"),
        RESOURCE_PACK("game.resource-pack", "", Migrate.PROPS, "resource-pack"),
        RESOURCE_PACK_HASH("game.resource-pack-hash", "", Migrate.PROPS, "resource-pack-hash"),
        SNOOPER_ENABLED("server.snooper-enabled", false, Migrate.PROPS, "snooper-enabled"),

        // critters
        SPAWN_MONSTERS("creatures.enable.monsters", true, Migrate.PROPS, "spawn-monsters"),
        SPAWN_ANIMALS("creatures.enable.animals", true, Migrate.PROPS, "spawn-animals"),
        SPAWN_NPCS("creatures.enable.npcs", true, Migrate.PROPS, "spawn-npcs"),
        MONSTER_LIMIT("creatures.limit.monsters", 70, Migrate.BUKKIT, "spawn-limits.monsters"),
        ANIMAL_LIMIT("creatures.limit.animals", 15, Migrate.BUKKIT, "spawn-limits.animals"),
        WATER_ANIMAL_LIMIT("creatures.limit.water", 5, Migrate.BUKKIT, "spawn-limits.water-animals"),
        AMBIENT_LIMIT("creatures.limit.ambient", 15, Migrate.BUKKIT, "spawn-limits.ambient"),
        MONSTER_TICKS("creatures.ticks.monsters", 1, Migrate.BUKKIT, "ticks-per.monster-spawns"),
        ANIMAL_TICKS("creatures.ticks.animal", 400, Migrate.BUKKIT, "ticks-per.animal-spawns"),

        // folders
        PLUGIN_FOLDER("folders.plugins", "plugins"),
        UPDATE_FOLDER("folders.update", "update", Migrate.BUKKIT, "settings.update-folder"),
        WORLD_FOLDER("folders.worlds", "worlds", Migrate.BUKKIT, "settings.world-container"),

        // files
        PERMISSIONS_FILE("files.permissions", "permissions.yml", Migrate.BUKKIT, "settings.permissions-file"),
        COMMANDS_FILE("files.commands", "commands.yml"),
        HELP_FILE("files.help", "help.yml"),

        // advanced
        CONNECTION_THROTTLE("advanced.connection-throttle", 4000, Migrate.BUKKIT, "settings.connection-throttle"),
        //PING_PACKET_LIMIT("advanced.ping-packet-limit", 100, Migrate.BUKKIT, "settings.ping-packet-limit"),
        PLAYER_IDLE_TIMEOUT("advanced.idle-timeout", 0, Migrate.PROPS, "player-idle-timeout"),
        WARN_ON_OVERLOAD("advanced.warn-on-overload", true, Migrate.BUKKIT, "settings.warn-on-overload"),
        EXACT_LOGIN_LOCATION("advanced.exact-login-location", false, Migrate.BUKKIT, "settings.use-exact-login-location"),
        PLUGIN_PROFILING("advanced.plugin-profiling", false, Migrate.BUKKIT, "settings.plugin-profiling"),
        WARNING_STATE("advanced.deprecated-verbose", "false", Migrate.BUKKIT, "settings.deprecated-verbose"),
        COMPRESSION_THRESHOLD("advanced.compression-threshold", 256, Migrate.PROPS, "network-compression-threshold"),
        PROXY_SUPPORT("advanced.proxy-support", false),
        PLAYER_SAMPLE_COUNT("advanced.player-sample-count", 12),
        METRICS("advanced.metrics", true),
        METRICS_UUID("advanced.metrics-server-uuid", UUID.randomUUID().toString()),
        GPGPU("advanced.gpgpu", false),
        GPGPU_ANY_DEVICE("advanced.gpgpu-use-any-device", false),
        RUN_CLIENT("advanced.run-glowclient", false),

        // query rcon etc
        QUERY_ENABLED("extras.query-enabled", false, Migrate.PROPS, "enable-query"),
        QUERY_PORT("extras.query-port", 25614, Migrate.PROPS, "query.port"),
        QUERY_PLUGINS("extras.query-plugins", true, Migrate.BUKKIT, "settings.query-plugins"),
        RCON_ENABLED("extras.rcon-enabled", false, Migrate.PROPS, "enable-rcon"),
        RCON_PASSWORD("extras.rcon-password", "glowstone", Migrate.PROPS, "rcon.password"),
        RCON_PORT("extras.rcon-port", 25575, Migrate.PROPS, "rcon.port"),
        RCON_COLORS("extras.rcon-colors", true),

        // level props
        LEVEL_NAME("world.name", "world", Migrate.PROPS, "level-name"),
        LEVEL_SEED("world.seed", "", Migrate.PROPS, "level-seed"),
        LEVEL_TYPE("world.level-type", "DEFAULT", Migrate.PROPS, "level-type"),
        SPAWN_RADIUS("world.spawn-radius", 16, Migrate.PROPS, "spawn-protection"),
        VIEW_DISTANCE("world.view-distance", 8, Migrate.PROPS, "view-distance"),
        GENERATE_STRUCTURES("world.gen-structures", true, Migrate.PROPS, "generate-structures"),
        ALLOW_NETHER("world.allow-nether", true, Migrate.PROPS, "allow-nether"),
        ALLOW_END("world.allow-end", true, Migrate.BUKKIT, "settings.allow-end"),
        PERSIST_SPAWN("world.keep-spawn-loaded", true),
        POPULATE_ANCHORED_CHUNKS("world.populate-anchored-chunks", true),
        WATER_CLASSIC("world.classic-style-water", false),
        DISABLE_GENERATION("world.disable-generation", false),

        // database
        DB_DRIVER("database.driver", "org.sqlite.JDBC", Migrate.BUKKIT, "database.driver"),
        DB_URL("database.url", "jdbc:sqlite:config/database.db", Migrate.BUKKIT, "database.url"),
        DB_USERNAME("database.username", "glowstone", Migrate.BUKKIT, "database.username"),
        DB_PASSWORD("database.password", "nether", Migrate.BUKKIT, "database.password"),
        DB_ISOLATION("database.isolation", "SERIALIZABLE", Migrate.BUKKIT, "database.isolation"),
        ;

        private final String path;
        private final Object def;
        private final Migrate migrate;
        private final String migratePath;

        Key(String path, Object def) {
            this(path, def, null, null);
        }

        Key(String path, Object def, Migrate migrate, String migratePath) {
            this.path = path;
            this.def = def;
            this.migrate = migrate;
            this.migratePath = migratePath;
        }

        @Override
        public String toString() {
            return name() + "(" + path + ", " + def + ")";
        }
    }

    private enum Migrate {
        BUKKIT, PROPS
    }

}
