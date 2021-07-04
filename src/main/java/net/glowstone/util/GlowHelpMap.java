package net.glowstone.util;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.help.IndexHelpTopic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An implementation of {@link HelpMap}.
 *
 * <p>See <a href="http://wiki.bukkit.org/Help.yml">http://wiki.bukkit.org/Help.yml</a>
 */
public final class GlowHelpMap implements HelpMap {

    private static final Comparator<String> NAME_COMPARE = HelpTopicComparator
            .topicNameComparatorInstance();
    private static final Comparator<HelpTopic> TOPIC_COMPARE = HelpTopicComparator
            .helpTopicComparatorInstance();

    private final GlowServer server;

    private final Map<String, HelpTopic> helpTopics;
    private final Map<Class, HelpTopicFactory<Command>> topicFactoryMap = new HashMap<>();
    private final Set<String> ignoredPlugins = new HashSet<>();

    private final Set<HelpTopic> indexTopics = new TreeSet<>(TOPIC_COMPARE);
    private HelpTopic defaultTopic;
    private boolean commandsInIndex = true;

    /**
     * Creates the instance for the given server.
     *
     * @param server the server
     */
    public GlowHelpMap(GlowServer server) {
        this.server = server;
        helpTopics = new TreeMap<>(NAME_COMPARE);
        defaultTopic
                = new IndexHelpTopic("Index", null, null, indexTopics, "Use /help [n] to get page"
                + " n of help.");
    }

    @Override
    public synchronized HelpTopic getHelpTopic(String topicName) {
        if (topicName.isEmpty()) {
            return defaultTopic;
        }

        return helpTopics.get(topicName);
    }

    @Override
    public Collection<HelpTopic> getHelpTopics() {
        return helpTopics.values();
    }

    @Override
    public synchronized void addTopic(HelpTopic topic) {
        indexTopics.add(topic);
        addPrivateTopic(topic);
    }

    @Override
    public void clear() {
        helpTopics.clear();
        topicFactoryMap.clear();
        ignoredPlugins.clear();
        indexTopics.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerHelpTopicFactory(Class commandClass, HelpTopicFactory factory) {
        if (!Command.class.isAssignableFrom(commandClass) && !CommandExecutor.class
                .isAssignableFrom(commandClass)) {
            throw new IllegalArgumentException("commandClass must implement either Command or "
                    + "CommandExecutor!");
        }
        topicFactoryMap.put(commandClass, factory);
    }

    @Override
    public List<String> getIgnoredPlugins() {
        return new ArrayList<>(ignoredPlugins);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    private void addCommandTopic(HelpTopic topic) {
        if (commandsInIndex) {
            addTopic(topic);
        } else {
            addPrivateTopic(topic);
        }
    }

    private void addPrivateTopic(HelpTopic topic) {
        if (!helpTopics.containsKey(topic.getName())) {
            helpTopics.put(topic.getName(), topic);
        }
    }

    private String color(String text) {
        return text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Reads the general topics from help.yml and adds them to the help index.
     *
     * @param config The configuration to read from.
     */
    public void loadConfig(ConfigurationSection config) {
        // general topics
        ConfigurationSection general = config.getConfigurationSection("general-topics");
        if (general != null) {
            for (String key : general.getKeys(false)) {
                ConfigurationSection topic = general.getConfigurationSection(key);
                if (topic != null) {
                    String shortText = topic.getString("shortText", "");
                    String fullText = topic.getString("fullText", "");
                    if (!shortText.isEmpty()) {
                        if (fullText.isEmpty()) {
                            fullText = shortText;
                        } else {
                            fullText = shortText + "\n" + ChatColor.RESET + fullText;
                        }
                    }
                    addTopic(new GeneralHelpTopic(key, color(shortText), color(fullText), topic
                            .getString("permission", null)));
                }
            }
        }

        // index topics
        ConfigurationSection index = config.getConfigurationSection("index-topics");
        if (index != null) {
            for (String key : index.getKeys(false)) {
                ConfigurationSection topic = index.getConfigurationSection(key);
                if (topic != null) {
                    String shortText = color(topic.getString("shortText", ""));
                    String preamble = color(topic.getString("preamble", null));
                    String permission = topic.getString("permission", null);
                    HelpTopic helpTopic = new LazyIndexTopic(key, shortText, permission, topic
                            .getStringList("commands"), preamble);
                    if (key.equals("Default")) {
                        defaultTopic = helpTopic;
                    } else {
                        addTopic(helpTopic);
                    }
                }
            }
        }

        // ignore plugins and command topics settings
        ignoredPlugins.addAll(config.getStringList("ignore-plugins"));
        commandsInIndex = config.getBoolean("command-topics-in-master-index", true);
    }

    /**
     * Processes all the commands registered in the server and creates help topics for them.
     */
    public synchronized void initializeCommands() {
        // Don't load any automatic help topics if All is ignored
        if (ignoredPlugins.contains("All")) {
            return;
        }

        Collection<Command> commands = server.getCommandMap().getCommands();
        // Initialize help topics from the server's command map
        outer:
        for (Command command : commands) {
            if (commandInIgnoredPlugin(command)) {
                continue;
            }

            // Register a topic
            for (Entry<Class, HelpTopicFactory<Command>> entry : topicFactoryMap.entrySet()) {
                if (((Class<?>) entry.getKey()).isAssignableFrom(command.getClass())) {
                    HelpTopic t = entry.getValue().createTopic(command);
                    if (t != null) {
                        addCommandTopic(t);
                    }
                    continue outer;
                }
                if (command instanceof PluginCommand && ((Class<?>) entry.getKey())
                        .isAssignableFrom(((PluginCommand) command).getExecutor().getClass())) {
                    HelpTopic t = entry.getValue().createTopic(command);
                    if (t != null) {
                        addCommandTopic(t);
                    }
                    continue outer;
                }
            }
            addCommandTopic(new GenericCommandHelpTopic(command));
        }

        // Alias topics for commands
        Set<HelpTopic> aliases = new TreeSet<>(TOPIC_COMPARE);
        for (Command command : commands) {
            if (commandInIgnoredPlugin(command)) {
                continue;
            }
            HelpTopic original = getHelpTopic("/" + command.getLabel());
            if (original != null) {
                for (String alias : command.getAliases()) {
                    HelpTopic aliasTopic = new AliasTopic("/" + alias, original);
                    if (!helpTopics.containsKey(aliasTopic.getName())) {
                        aliases.add(aliasTopic);
                        addPrivateTopic(aliasTopic);
                    }
                }
            }
        }

        // Aliases index topic
        if (!aliases.isEmpty()) {
            addTopic(new IndexHelpTopic("Aliases", "Lists command aliases", null, aliases, null));
        }

        // Initialize plugin-level sub-topics
        Map<String, Set<HelpTopic>> pluginIndexes = new HashMap<>();
        for (Command command : commands) {
            String pluginName = getCommandPluginName(command);
            if (pluginName != null) {
                HelpTopic topic = getHelpTopic("/" + command.getLabel());
                if (topic != null) {
                    if (!pluginIndexes.containsKey(pluginName)) {
                        pluginIndexes.put(pluginName, new TreeSet<>(TOPIC_COMPARE));
                    }
                    pluginIndexes.get(pluginName).add(topic);
                }
            }
        }
        for (Entry<String, Set<HelpTopic>> entry : pluginIndexes.entrySet()) {
            String key = entry.getKey();
            addTopic(new IndexHelpTopic(key,
                    "All commands for " + key, null, entry.getValue(),
                    "Below is a list of all " + key + " commands:"));
        }
    }

    /**
     * Process topic amendments from help.yml.
     *
     * @param config The configuration to read from.
     */
    public void amendTopics(ConfigurationSection config) {
        ConfigurationSection amendedTopics = config.getConfigurationSection("amended-topics");
        if (amendedTopics != null) {
            for (String key : amendedTopics.getKeys(false)) {
                HelpTopic target = getHelpTopic(key);
                if (target == null) {
                    continue;
                }
                ConfigurationSection amend = amendedTopics.getConfigurationSection(key);
                if (amend == null) {
                    continue;
                }

                target.amendTopic(color(amend.getString("shortText")), color(amend
                        .getString("fullText")));
                String perm = amend.getString("permission", null);
                if (perm != null) {
                    // empty string can be specified to remove permission
                    target.amendCanSee(perm.isEmpty() ? null : perm);
                }
            }
        }
    }

    private String getCommandPluginName(Command command) {
        if (command instanceof BukkitCommand) {
            return "Bukkit";
        }
        if (command instanceof PluginIdentifiableCommand) {
            return ((PluginIdentifiableCommand) command).getPlugin().getName();
        }
        return null;
    }

    private boolean commandInIgnoredPlugin(Command command) {
        String name = getCommandPluginName(command);
        return name != null && ignoredPlugins.contains(name);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Help topic subclasses

    private static class GeneralHelpTopic extends HelpTopic {

        public GeneralHelpTopic(String name, String shortText, String fullText, String permission) {
            this.name = name;
            this.shortText = shortText;
            this.fullText = fullText;
            amendedPermission = permission;
        }

        @Override
        public boolean canSee(CommandSender sender) {
            return sender instanceof ConsoleCommandSender || amendedPermission == null || sender
                    .hasPermission(amendedPermission);
        }
    }

    private static class AliasTopic extends HelpTopic {

        private final HelpTopic original;

        public AliasTopic(String name, HelpTopic original) {
            this.name = name;
            shortText = ChatColor.YELLOW + "Alias for " + ChatColor.WHITE + original.getName();
            this.original = original;
        }

        @Override
        public boolean canSee(CommandSender player) {
            return original.canSee(player);
        }

        @Override
        public String getFullText(CommandSender sender) {
            return shortText + "\n" + original.getFullText(sender);
        }
    }

    private class LazyIndexTopic extends IndexHelpTopic {

        private Collection<String> topics;

        public LazyIndexTopic(String name, String shortText, String permission,
                Collection<String> topics, String preamble) {
            super(name, shortText, permission, Collections.emptyList(), preamble);
            this.topics = topics;
        }

        @Override
        public String getFullText(CommandSender sender) {
            if (topics != null) {
                List<HelpTopic> list = new ArrayList<>(topics.size());
                for (String name : topics) {
                    HelpTopic topic = getHelpTopic(name);
                    if (topic != null) {
                        list.add(topic);
                    }
                }
                setTopicsCollection(list);
                topics = null;
            }
            return super.getFullText(sender);
        }
    }
}
