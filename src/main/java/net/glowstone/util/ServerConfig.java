package net.glowstone.util;

import net.glowstone.GlowServer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Utilities for handling the server configuration files.
 */
public class ServerConfig {

    /**
     * The directory configurations are stored in.
     */
    public static final File CONFIG_DIR = new File("config");

    /**
     * The main configuration file.
     */
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "glowstone.yml");

    /**
     * The actual configuration data.
     */
    private final YamlConfiguration config = new YamlConfiguration();

    public ServerConfig() {
        if (!CONFIG_DIR.isDirectory() && !CONFIG_DIR.mkdirs()) {
            GlowServer.logger.severe("Cannot create directory: " + CONFIG_DIR);
        }

        config.options().indent(4);
        setupDefaults();
    }

    private void setupDefaults() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("defaults/glowstone.yml");
        if (stream == null) {
            GlowServer.logger.warning("Could not find default configuration on classpath");
            return;
        }

        YamlConfiguration defaults = new YamlConfiguration();
        try {
            defaults.load(stream);
        } catch (InvalidConfigurationException e) {
            GlowServer.logger.log(Level.SEVERE, "Default configuration was invalid", e);
            return;
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Default configuration could not be read", e);
            return;
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        config.setDefaults(defaults);
        config.options().copyDefaults(true);
    }

    public void load() {
        // load config
        if (!CONFIG_FILE.exists()) {
            // create config
            if (!CONFIG_DIR.isDirectory()) {
                return;  // won't be able to save
            }

            // migrate or save default file
            if (migrate()) {
                GlowServer.logger.info("Migrated configuration from CraftBukkit");
            } else {
                GlowServer.logger.info("Created default configuration");
            }
        } else {
            // load config
            try {
                config.load(CONFIG_FILE);
            } catch (IOException e) {
                GlowServer.logger.log(Level.SEVERE, "Failed to read config: " + CONFIG_FILE, e);
                return;
            } catch (InvalidConfigurationException e) {
                report(e);
                return;
            }
        }

        // save config, including any new defaults
        try {
            config.save(CONFIG_FILE);
        } catch (IOException e) {
            GlowServer.logger.log(Level.SEVERE, "Failed to write config: " + CONFIG_FILE, e);
        }
    }

    private void report(InvalidConfigurationException e) {
        if (e.getCause() instanceof YAMLException) {
            GlowServer.logger.severe("Config file " + CONFIG_FILE + " isn't valid! " + e.getCause());
        } else if ((e.getCause() == null) || (e.getCause() instanceof ClassCastException)) {
            GlowServer.logger.severe("Config file " + CONFIG_FILE + " isn't valid!");
        } else {
            GlowServer.logger.log(Level.SEVERE, "Cannot load " + CONFIG_FILE + ": " + e.getCause().getClass(), e);
        }
    }

    public void set(Key key, Object value) {
        GlowServer.logger.info("config: set " + key + " = " + value);
        config.set(key.path, value);
    }

    public String getString(Key key) {
        GlowServer.logger.info("config: getString " + key);
        return config.getString(key.path);
    }

    public int getInt(Key key) {
        if (key != Key.VIEW_DISTANCE) GlowServer.logger.info("config: getInt " + key);
        return config.getInt(key.path);
    }

    public boolean getBoolean(Key key) {
        GlowServer.logger.info("config: getBoolean " + key);
        return config.getBoolean(key.path);
    }

    public ConfigurationSection getSection(String section) {
        GlowServer.logger.info("config: getSection " + section);
        return config.getConfigurationSection(section);
    }

    private boolean migrate() {
        boolean migrateStatus = false;

        File bukkitYml = new File("bukkit.yml");
        if (bukkitYml.exists()) {
            YamlConfiguration bukkit = new YamlConfiguration();
            try {
                bukkit.load(bukkitYml);
            } catch (InvalidConfigurationException e) {
                report(e);
            } catch (IOException ignored) {
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.BUKKIT && bukkit.contains(key.migratePath)) {
                    config.set(key.path, bukkit.get(key.migratePath));
                    migrateStatus = true;
                }
            }

            config.set("aliases", bukkit.get("aliases"));
            config.set("database", bukkit.get("database"));
            config.set("worlds", bukkit.get("worlds"));
        }

        File serverProps = new File("server.properties");
        if (serverProps.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(serverProps));
            } catch (IOException ignored) {
            }

            for (Key key : Key.values()) {
                if (key.migrate == Migrate.PROPS && props.containsKey(key.migratePath)) {
                    config.set(key.path, props.get(key.migratePath));
                    migrateStatus = true;
                }
            }
        }

        return migrateStatus;
    }

    /**
     * An enum containing configuration keys used by the server.
     */
    public static enum Key {
        // server
        SERVER_IP("server.ip", Migrate.PROPS, "server-ip"),
        SERVER_PORT("server.port", Migrate.PROPS, "server-port"),
        SERVER_NAME("server.name", Migrate.PROPS, "server-name"),
        LOG_FILE("server.log-file"),
        FUZZY_COMMANDS("server.fuzzy-commands"),
        ONLINE_MODE("server.online-mode", Migrate.PROPS, "online-mode"),
        MAX_PLAYERS("server.max-players", Migrate.PROPS, "max-players"),
        WHITELIST("server.whitelisted", Migrate.PROPS, "white-list"),
        MOTD("server.motd", Migrate.PROPS, "motd"),
        SHUTDOWN_MESSAGE("server.shutdown-message", Migrate.BUKKIT, "settings.shutdown-message"),

        // folders
        PLUGIN_FOLDER("folders.plugins"),
        UPDATE_FOLDER("folders.update", Migrate.BUKKIT, "settings.update-folder"),
        WORLD_FOLDER("folders.worlds", Migrate.BUKKIT, "settings.world-container"),

        // advanced
        CONNECTION_THROTTLE("advanced.connection-throttle", Migrate.BUKKIT, "settings.connection-throttle"),
        PING_PACKET_LIMIT("advanced.ping-packet-limit", Migrate.BUKKIT, "settings.ping-packet-limit"),
        PLAYER_IDLE_TIMEOUT("advanced.idle-timeout", Migrate.PROPS, "player-idle-timeout"),
        WARN_ON_OVERLOAD("advanced.warn-on-overload", Migrate.BUKKIT, "settings.warn-on-overload"),
        PERMISSIONS_FILE("advanced.permissions-file", Migrate.BUKKIT, "settings.permissions-file"),
        EXACT_LOGIN_LOCATION("advanced.exact-login-location", Migrate.BUKKIT, "settings.use-exact-login-location"),
        PLUGIN_PROFILING("advanced.plugin-profiling", Migrate.BUKKIT, "settings.plugin-profiling"),
        WARNING_STATE("advanced.deprecated-verbose", Migrate.BUKKIT, "settings.deprecated-verbose"),

        // query rcon etc
        /*QUERY_ENABLED("extras.query-enabled", Migrate.PROPS, "enable-query"),
        QUERY_PORT("extras.query-port", Migrate.PROPS, "query.port"),
        QUERY_PLUGINS("extras.query-plugins", Migrate.BUKKIT, "settings.query-plugins"),
        RCON_ENABLED("extras.rcon-enabled", Migrate.PROPS, "enable-rcon"),
        RCON_PASSWORD("extras.rcon-password", Migrate.PROPS, "rcon.password"),
        RCON_PORT("extras.rcon-port", Migrate.PROPS, "rcon.port"),*/

        // level props
        LEVEL_NAME("world.name", Migrate.PROPS, "level-name"),
        LEVEL_SEED("world.seed", Migrate.PROPS, "level-seed"),
        LEVEL_TYPE("world.level-type", Migrate.PROPS, "level-type"),
        SPAWN_RADIUS("world.spawn-radius", Migrate.PROPS, "spawn-protection"),
        VIEW_DISTANCE("world.view-distance", Migrate.PROPS, "view-distance"),
        GENERATE_STRUCTURES("world.gen-structures", Migrate.PROPS, "generate-structures"),
        GENERATOR_SETTINGS("world.gen-settings", Migrate.PROPS, "generator-settings"),
        ALLOW_NETHER("world.allow-nether", Migrate.PROPS, "allow-nether"),
        ALLOW_END("world.allow-end", Migrate.BUKKIT, "settings.allow-end"),

        // game props
        GAMEMODE("game.gamemode", Migrate.PROPS, "gamemode"),
        FORCE_GAMEMODE("game.gamemode-force", Migrate.PROPS, "force-gamemode"),
        DIFFICULTY("game.difficulty", Migrate.PROPS, "difficulty"),
        HARDCORE("game.hardcore", Migrate.PROPS, "hardcore"),
        PVP_ENABLED("game.pvp", Migrate.PROPS, "pvp"),
        MAX_BUILD_HEIGHT("game.max-build-height", Migrate.PROPS, "max-build-height"),
        ANNOUNCE_ACHIEVEMENTS("game.announce-achievements", Migrate.PROPS, "announce-player-achievements"),

        // server.properties keys
        ALLOW_FLIGHT("game.allow-flight", Migrate.PROPS, "allow-flight"),
        ENABLE_COMMAND_BLOCK("game.command-blocks", Migrate.PROPS, "enable-command-block"),
        //OP_PERMISSION_LEVEL(null, Migrate.PROPS, "op-permission-level"),
        RESOURCE_PACK("game.resource-pack", Migrate.PROPS, "resource-pack"),
        SNOOPER_ENABLED("server.snooper-enabled", Migrate.PROPS, "snooper-enabled"),

        // critters
        SPAWN_MONSTERS("creatures.enable.monsters", Migrate.PROPS, "spawn-monsters"),
        SPAWN_ANIMALS("creatures.enable.animals", Migrate.PROPS, "spawn-animals"),
        SPAWN_NPCS("creatures.enable.npcs", Migrate.PROPS, "spawn-npcs"),
        MONSTER_LIMIT("creatures.limit.monsters", Migrate.BUKKIT, "spawn-limits.monsters"),
        ANIMAL_LIMIT("creatures.limit.animals", Migrate.BUKKIT, "spawn-limits.animals"),
        WATER_ANIMAL_LIMIT("creatures.limit.water", Migrate.BUKKIT, "spawn-limits.water-animals"),
        AMBIENT_LIMIT("creatures.limit.ambient", Migrate.BUKKIT, "spawn-limits.ambient"),
        MONSTER_TICKS("creatures.ticks.monsters", Migrate.BUKKIT, "ticks-per.monster-spawns"),
        ANIMAL_TICKS("creatures.ticks.animal", Migrate.BUKKIT, "ticks-per.animal-spawns"),

        ;

        private final String path;
        private final Migrate migrate;
        private final String migratePath;

        private Key(String path) {
            this(path, null, null);
        }

        private Key(String path, Migrate migrate, String migratePath) {
            this.path = path;
            this.migrate = migrate;
            this.migratePath = migratePath;
        }

        @Override
        public String toString() {
            return name() + "(" + path + ")";
        }
    }

    private static enum Migrate {
        BUKKIT, PROPS
    }

}
