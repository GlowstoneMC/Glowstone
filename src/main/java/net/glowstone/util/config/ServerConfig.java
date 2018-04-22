package net.glowstone.util.config;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.glowstone.util.config.ServerConfig.Validators.typeCheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.logging.Level;
import lombok.Getter;
import net.glowstone.GlowServer;
import net.glowstone.util.CompatibilityBundle;
import net.glowstone.util.DynamicallyTypedMap;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.WorldType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * Utilities for handling the server configuration files.
 */
public final class ServerConfig implements DynamicallyTypedMap<ServerConfig.Key> {

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
     * @param directory  The config directory, or null for default.
     * @param configFile The config file, or null for default.
     * @param parameters The command-line parameters used as overrides.
     */
    public ServerConfig(File directory, File configFile, Map<Key, Object> parameters) {
        checkNotNull(directory);
        checkNotNull(configFile);
        checkNotNull(parameters);

        this.directory = directory;
        this.configFile = configFile;
        this.parameters = parameters;

        config.options().indent(4).copyHeader(true).header(
                "glowstone.yml is the main configuration file for a Glowstone server\n"
                        + "It contains everything from server.properties and bukkit.yml in a\n"
                        + "normal CraftBukkit installation.\n\n"
                        + "Configuration entries are documented on the wiki: "
                        + "https://github.com/GlowstoneMC/Glowstone/wiki/Configuration-Guide\n"
                        + "For help, join us on Discord: https://discord.gg/TFJqhsC");
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

    @Override
    public String getString(Key key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key).toString();
        }
        String string = config.getString(key.path, key.def.toString());
        parameters.put(key, string);
        return string;
    }

    @Override
    public int getInt(Key key) {
        if (parameters.containsKey(key)) {
            return (Integer) parameters.get(key);
        }
        int integer = config.getInt(key.path, (Integer) key.def);
        parameters.put(key, integer);
        return integer;
    }

    @Override
    public boolean getBoolean(Key key) {
        if (parameters.containsKey(key)) {
            return (Boolean) parameters.get(key);
        }
        boolean bool = config.getBoolean(key.path, (Boolean) key.def);
        parameters.put(key, bool);
        return bool;
    }

    /**
     * Retrieves a section as a list of maps.
     *
     * @param key the key to look up
     * @return the value as a list of maps
     */
    @SuppressWarnings("unchecked")
    public List<Map<?, ?>> getMapList(Key key) {
        if (parameters.containsKey(key)) {
            return (List<Map<?, ?>>) parameters.get(key);
        }
        // there's no get or default method for the getMapList method, so using contains.
        if (!config.contains(key.path)) {
            parameters.put(key, key.def);
            return (List<Map<?, ?>>) key.def;
        }
        return config.getMapList(key.path);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Fancy stuff

    /**
     * Returns the file that contains a given setting. If it doesn't exist, it is created and
     * populated with defaults.
     *
     * @param key the configuration setting
     * @return the file containing that setting
     */
    public ConfigurationSection getConfigFile(Key key) {
        String filename = getString(key);
        if (extraConfig.containsKey(filename)) {
            return extraConfig.get(filename);
        }

        YamlConfiguration conf = new YamlConfiguration();
        File file = getFile(filename);
        File migrateFrom = new File(key.def.toString());

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

    public File getFile(String filename) {
        return new File(directory, filename);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Load and internals

    /**
     * Loads the server config from disk. If it doesn't exist, the default config is written,
     * creating the folder if necessary. If it's in the old bukkit.yml format and/or incomplete, it
     * is converted to canonical form and saved.
     */
    public void load() {
        // load extra config files again next time they're needed
        extraConfig.clear();

        boolean changed = false;

        // create default file if needed
        if (!configFile.exists()) {
            GlowServer.logger.info("Creating default config: " + configFile);

            // create config directory
            if (!directory.isDirectory() && !directory.mkdirs()) {
                GlowServer.logger.severe("Cannot create directory: " + directory);
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
                } else if (key.validator != null) {
                    // validate existing values
                    Object val = config.get(key.path);
                    if (!key.validator.test(val)) {
                        GlowServer.logger.warning(
                                "Invalid config value for '" + key.path + "' (" + val + "), "
                                        + "resetting to default (" + key.def + ")");
                        config.set(key.path, key.def);
                        changed = true;
                    }
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
            GlowServer.logger
                    .log(Level.SEVERE, "Cannot load " + file + ": " + e.getCause().getClass(), e);
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
                            GlowServer.logger.log(Level.WARNING,
                                    "Could not migrate " + key.migratePath + " from "
                                            + serverProps, e);
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
        SERVER_IP("server.ip", "", Migrate.PROPS, "server-ip", String.class::isInstance),
        SERVER_PORT("server.port", 25565, Migrate.PROPS, "server-port", Validators.PORT),
        SERVER_NAME("server.name", "Glowstone Server", Migrate.PROPS, "server-name",
                String.class::isInstance),
        LOG_FILE("server.log-file", "logs/log-%D.txt", String.class::isInstance),
        ONLINE_MODE("server.online-mode", true, Migrate.PROPS, "online-mode",
                Boolean.class::isInstance),
        MAX_PLAYERS("server.max-players", 20, Migrate.PROPS, "max-players",
                Validators.POSITIVE_INTEGER),
        WHITELIST("server.whitelisted", false, Migrate.PROPS, "white-list",
                Boolean.class::isInstance),
        MOTD("server.motd", "A Glowstone server", Migrate.PROPS, "motd",
                String.class::isInstance),
        SHUTDOWN_MESSAGE("server.shutdown-message", "Server shutting down.", Migrate.BUKKIT,
                "settings.shutdown-message", String.class::isInstance),
        ALLOW_CLIENT_MODS("server.allow-client-mods", true, Boolean.class::isInstance),

        // console
        USE_JLINE("console.use-jline", true, Boolean.class::isInstance),
        CONSOLE_PROMPT("console.prompt", "> ", String.class::isInstance),
        CONSOLE_DATE("console.date-format", "HH:mm:ss", String.class::isInstance),
        CONSOLE_LOG_DATE("console.log-date-format", "yyyy/MM/dd HH:mm:ss",
                String.class::isInstance),

        // game props
        GAMEMODE("game.gamemode", "SURVIVAL", Migrate.PROPS, "gamemode",
                Validators.forEnum(GameMode.class)),
        FORCE_GAMEMODE("game.gamemode-force", false, Migrate.PROPS, "force-gamemode",
                Boolean.class::isInstance),
        DIFFICULTY("game.difficulty", "NORMAL", Migrate.PROPS, "difficulty",
                Validators.forEnum(Difficulty.class)),
        HARDCORE("game.hardcore", false, Migrate.PROPS, "hardcore",
                Boolean.class::isInstance),
        PVP_ENABLED("game.pvp", true, Migrate.PROPS, "pvp",
                Boolean.class::isInstance),
        MAX_BUILD_HEIGHT("game.max-build-height", 256, Migrate.PROPS, "max-build-height",
                Validators.POSITIVE_INTEGER),
        ANNOUNCE_ACHIEVEMENTS("game.announce-achievements", true, Migrate.PROPS,
                "announce-player-achievements", Boolean.class::isInstance),

        // server.properties keys
        ALLOW_FLIGHT("game.allow-flight", false, Migrate.PROPS, "allow-flight",
                Boolean.class::isInstance),
        ENABLE_COMMAND_BLOCK("game.command-blocks", false, Migrate.PROPS, "enable-command-block",
                Boolean.class::isInstance),
        //OP_PERMISSION_LEVEL(null, Migrate.PROPS, "op-permission-level"),
        RESOURCE_PACK("game.resource-pack", "", Migrate.PROPS, "resource-pack",
                String.class::isInstance),
        RESOURCE_PACK_HASH("game.resource-pack-hash", "", Migrate.PROPS, "resource-pack-hash",
                String.class::isInstance),
        SNOOPER_ENABLED("server.snooper-enabled", false, Migrate.PROPS, "snooper-enabled",
                Boolean.class::isInstance),
        PREVENT_PROXY("server.prevent-proxy-connections", true, Migrate.PROPS,
                "prevent-proxy-connections", Boolean.class::isInstance),

        // creatures
        SPAWN_MONSTERS("creatures.enable.monsters", true, Migrate.PROPS, "spawn-monsters",
                Boolean.class::isInstance),
        SPAWN_ANIMALS("creatures.enable.animals", true, Migrate.PROPS, "spawn-animals",
                Boolean.class::isInstance),
        SPAWN_NPCS("creatures.enable.npcs", true, Migrate.PROPS, "spawn-npcs",
                Boolean.class::isInstance),
        MONSTER_LIMIT("creatures.limit.monsters", 70, Migrate.BUKKIT, "spawn-limits.monsters",
                Validators.NON_NEGATIVE_INTEGER),
        ANIMAL_LIMIT("creatures.limit.animals", 15, Migrate.BUKKIT, "spawn-limits.animals",
                Validators.NON_NEGATIVE_INTEGER),
        WATER_ANIMAL_LIMIT("creatures.limit.water", 5, Migrate.BUKKIT,
                "spawn-limits.water-animals",
                Validators.NON_NEGATIVE_INTEGER),
        AMBIENT_LIMIT("creatures.limit.ambient", 15, Migrate.BUKKIT, "spawn-limits.ambient",
                Validators.NON_NEGATIVE_INTEGER),
        MONSTER_TICKS("creatures.ticks.monsters", 1, Migrate.BUKKIT, "ticks-per.monster-spawns",
                Validators.NON_NEGATIVE_INTEGER),
        ANIMAL_TICKS("creatures.ticks.animal", 400, Migrate.BUKKIT, "ticks-per.animal-spawns",
                Validators.NON_NEGATIVE_INTEGER),

        // folders
        PLUGIN_FOLDER("folders.plugins", "plugins", Validators.PATH),
        UPDATE_FOLDER("folders.update", "update", Migrate.BUKKIT, "settings.update-folder",
                Validators.PATH),
        WORLD_FOLDER("folders.worlds", "worlds", Migrate.BUKKIT, "settings.world-container",
                Validators.PATH),
        LIBRARIES_FOLDER("folders.libraries", "lib", Validators.PATH),

        // files
        PERMISSIONS_FILE("files.permissions", "permissions.yml", Migrate.BUKKIT,
                "settings.permissions-file", Validators.PATH),
        COMMANDS_FILE("files.commands", "commands.yml", Validators.PATH),
        HELP_FILE("files.help", "help.yml", Validators.PATH),

        // advanced
        CONNECTION_THROTTLE("advanced.connection-throttle", 4000, Migrate.BUKKIT,
                "settings.connection-throttle",
                Validators.NON_NEGATIVE_INTEGER),
        //PING_PACKET_LIMIT(
        //        "advanced.ping-packet-limit", 100, Migrate.BUKKIT, "settings.ping-packet-limit"),
        PLAYER_IDLE_TIMEOUT("advanced.idle-timeout", 0, Migrate.PROPS, "player-idle-timeout",
                Validators.NON_NEGATIVE_INTEGER),
        WARN_ON_OVERLOAD("advanced.warn-on-overload", true, Migrate.BUKKIT,
                "settings.warn-on-overload", Boolean.class::isInstance),
        EXACT_LOGIN_LOCATION("advanced.exact-login-location", false, Migrate.BUKKIT,
                "settings.use-exact-login-location", Boolean.class::isInstance),
        PLUGIN_PROFILING("advanced.plugin-profiling", false, Migrate.BUKKIT,
                "settings.plugin-profiling", Boolean.class::isInstance),
        WARNING_STATE("advanced.deprecated-verbose", "false", Migrate.BUKKIT,
                "settings.deprecated-verbose"),
        COMPRESSION_THRESHOLD("advanced.compression-threshold", 256, Migrate.PROPS,
                "network-compression-threshold",
                typeCheck(Integer.class).and(value -> value >= -1)),
        PROXY_SUPPORT("advanced.proxy-support", false, Boolean.class::isInstance),
        PLAYER_SAMPLE_COUNT("advanced.player-sample-count", 12,
                Validators.NON_NEGATIVE_INTEGER),
        GRAPHICS_COMPUTE("advanced.graphics-compute.enable", false),
        GRAPHICS_COMPUTE_ANY_DEVICE("advanced.graphics-compute.use-any-device", false,
                Boolean.class::isInstance),
        REGION_CACHE_SIZE("advanced.region-file.cache-size", 256,
                Validators.NON_NEGATIVE_INTEGER),
        REGION_COMPRESSION("advanced.region-file.compression", true,
                Boolean.class::isInstance),
        PROFILE_LOOKUP_TIMEOUT("advanced.profile-lookup-timeout", 5,
                Validators.NON_NEGATIVE_INTEGER),
        SUGGEST_PLAYER_NAMES_WHEN_NULL_TAB_COMPLETIONS(
                "advanced.suggest-player-name-when-null-tab-completions", true,
                Boolean.class::isInstance),

        // query rcon etc
        QUERY_ENABLED("extras.query-enabled", false, Migrate.PROPS, "enable-query",
                Boolean.class::isInstance),
        QUERY_PORT("extras.query-port", 25614, Migrate.PROPS, "query.port", Validators.PORT),
        QUERY_PLUGINS("extras.query-plugins", true, Migrate.BUKKIT, "settings.query-plugins",
                Boolean.class::isInstance),
        RCON_ENABLED("extras.rcon-enabled", false, Migrate.PROPS, "enable-rcon",
                Boolean.class::isInstance),
        RCON_PASSWORD("extras.rcon-password", "glowstone", Migrate.PROPS, "rcon.password",
                String.class::isInstance),
        RCON_PORT("extras.rcon-port", 25575, Migrate.PROPS, "rcon.port", Validators.PORT),
        RCON_COLORS("extras.rcon-colors", true,
                Boolean.class::isInstance),

        // level props
        LEVEL_NAME("world.name", "world", Migrate.PROPS, "level-name",
                String.class::isInstance),
        LEVEL_SEED("world.seed", "", Migrate.PROPS, "level-seed"),
        LEVEL_TYPE("world.level-type", "DEFAULT", Migrate.PROPS, "level-type", Validators
                .WORLD_TYPE),
        SPAWN_RADIUS("world.spawn-radius", 16, Migrate.PROPS, "spawn-protection",
                Validators.NON_NEGATIVE_INTEGER),
        VIEW_DISTANCE("world.view-distance", 8, Migrate.PROPS, "view-distance",
                Validators.POSITIVE_INTEGER),
        GENERATE_STRUCTURES("world.gen-structures", true, Migrate.PROPS, "generate-structures",
                Boolean.class::isInstance),
        ALLOW_NETHER("world.allow-nether", true, Migrate.PROPS, "allow-nether",
                Boolean.class::isInstance),
        ALLOW_END("world.allow-end", true, Migrate.BUKKIT, "settings.allow-end",
                Boolean.class::isInstance),
        PERSIST_SPAWN("world.keep-spawn-loaded", true,
                Boolean.class::isInstance),
        POPULATE_ANCHORED_CHUNKS("world.populate-anchored-chunks", true,
                Boolean.class::isInstance),
        WATER_CLASSIC("world.classic-style-water", false,
                Boolean.class::isInstance),
        DISABLE_GENERATION("world.disable-generation", false,
                Boolean.class::isInstance),

        // libraries
        LIBRARY_CHECKSUM_VALIDATION("libraries.checksum-validation", true,
                Boolean.class::isInstance),
        LIBRARY_REPOSITORY_URL("libraries.repository-url",
                "https://repo.glowstone.net/service/local/repositories/central/content/",
                String.class::isInstance),
        LIBRARY_DOWNLOAD_ATTEMPTS("libraries.download-attempts", 2,
                Validators.POSITIVE_INTEGER),
        COMPATIBILITY_BUNDLE("libraries.compatibility-bundle",
                CompatibilityBundle.CRAFTBUKKIT.name(), String.class::isInstance),
        LIBRARIES_LIST("libraries.list", Collections.emptyList());

        @Getter
        private final String path;
        private final Object def;
        private final Migrate migrate;
        private final String migratePath;
        private final Predicate validator;

        Key(String path, Object def) {
            this(path, def, null, null);
        }

        Key(String path, Object def, Predicate<?> validator) {
            this(path, def, null, null, validator);
        }

        Key(String path, Object def, Migrate migrate, String migratePath) {
            this(path, def, migrate, migratePath, null);
        }

        Key(String path, Object def, Migrate migrate, String migratePath, Predicate<?> validator) {
            this.path = path;
            this.def = def;
            this.migrate = migrate;
            this.migratePath = migratePath;
            this.validator = validator;
        }

        @Override
        public String toString() {
            return name() + "(" + path + ", " + def + ")";
        }
    }

    /**
     * A predicate wrapper to check if a value is a valid element of an enum.
     *
     * <p>See {@link Validators#forEnum(Class)}
     *
     * @param <T> the type of the enum
     */
    static final class EnumPredicate<T extends Enum> implements Predicate<String> {
        final Class<T> enumClass;

        EnumPredicate(Class<T> enumClass) {
            checkNotNull(enumClass);
            checkArgument(enumClass.isEnum());
            this.enumClass = enumClass;
        }

        @Override
        public boolean test(String value) {
            if (!typeCheck(String.class).test(value)) {
                return false;
            }
            if (value == null || value.isEmpty()) {
                return false;
            }
            try {
                Enum.valueOf(enumClass, value);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    static class Validators {
        /**
         * Checks if the value is positive (over zero).
         */
        static final Predicate<Number> POSITIVE = (number) -> number.doubleValue() > 0;
        /**
         * Checks if the value is integer-typed and positive.
         */
        static final Predicate<Integer> POSITIVE_INTEGER = typeCheck(Integer.class).and(
                POSITIVE);
        /**
         * Checks if the value is zero.
         */
        static final Predicate<Number> ZERO = (number) -> number.doubleValue() == 0;
        /**
         * Checks if the value is greater than (positive) or equal to zero.
         */
        static final Predicate<Number> ABSOLUTE = POSITIVE.or(ZERO);
        /**
         * Checks if the value is integer-typed and either positive or zero.
         */
        static final Predicate<?> NON_NEGATIVE_INTEGER = typeCheck(Integer.class).and(ABSOLUTE);
        /**
         * Checks if the value is a valid port number.
         */
        static final Predicate<Integer> PORT = typeCheck(Integer.class)
                .and(POSITIVE).and((number) -> number < 49152);
        /**
         * Checks if the value is a valid {@link WorldType} name.
         */
        static final Predicate<String> WORLD_TYPE = typeCheck(String.class)
                .and((value) -> WorldType.getByName(value) != null);

        /**
         * Creates a {@link EnumPredicate} that checks if the value is a member of the given enum
         * class.
         *
         * @param enumClass the enum class
         * @param <T>       the type of the enum
         * @return the predicate
         */
        static <T extends Enum> EnumPredicate<T> forEnum(Class<T> enumClass) {
            return new EnumPredicate<>(enumClass);
        }

        /**
         * Checks if the value is a valid file/directory path.
         *
         * <p>Note that the behavior of this predicate may be platform-dependent.
         */
        static final Predicate<String> PATH = typeCheck(String.class).and((value) -> {
            try {
                if (Paths.get(value) == null) {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }
            return true;
        });

        /**
         * Creates a {@link Predicate} that checks if the value is an instance of the
         * specified class.
         *
         * @param expected the expected class.
         * @return the predicate
         */
        static <T> Predicate<T> typeCheck(Class<T> expected) {
            return expected::isInstance;
        }
    }

    private enum Migrate {
        BUKKIT, PROPS
    }
}
