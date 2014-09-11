package net.glowstone.util;

import net.glowstone.GlowServer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.help.*;

import java.util.*;

/**
 * An implementation of {@link HelpMap}.
 */
public final class GlowHelpMap implements HelpMap {

    private HelpTopic defaultTopic;
    private final Map<String, HelpTopic> helpTopics;
    private final Map<Class, HelpTopicFactory<Command>> topicFactoryMap;
    private final GlowServer server;

    public GlowHelpMap(GlowServer server) {
        this.server = server;
        helpTopics = new TreeMap<>(HelpTopicComparator.topicNameComparatorInstance());
        topicFactoryMap = new HashMap<>();

        // todo: filter the index topic removing aliases and optionally commands
        this.defaultTopic = new IndexHelpTopic("Index", null, null, helpTopics.values(), "Use /help [n] to get page n of help.");
    }

    @Override
    public synchronized HelpTopic getHelpTopic(String topicName) {
        if (topicName.equals("")) {
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
        if (!helpTopics.containsKey(topic.getName())) {
            helpTopics.put(topic.getName(), topic);
        }
    }

    @Override
    public void clear() {
        helpTopics.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerHelpTopicFactory(Class commandClass, HelpTopicFactory factory) {
        if (!Command.class.isAssignableFrom(commandClass) && !CommandExecutor.class.isAssignableFrom(commandClass)) {
            throw new IllegalArgumentException("commandClass must implement either Command or CommandExecutor!");
        }
        topicFactoryMap.put(commandClass, factory);
    }

    @Override
    public List<String> getIgnoredPlugins() {
        return Arrays.asList();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals - based on CraftBukkit

    /**
     * Reads the general topics from help.yml and adds them to the help index.
     */
    public synchronized void initializeGeneralTopics() {
        // todo: read from help.yml
    }

    /**
     * Processes all the commands registered in the server and creates help topics for them.
     */
    public synchronized void initializeCommands() {
        // ** Load topics from highest to lowest priority order **
        // todo: ignore specified plugins
        Set<String> ignoredPlugins = Collections.emptySet();

        // Don't load any automatic help topics if All is ignored
        if (ignoredPlugins.contains("All")) {
            return;
        }

        // Initialize help topics from the server's command map
        outer:
        for (Command command : server.getCommandMap().getCommands()) {
            if (commandInIgnoredPlugin(command, ignoredPlugins)) {
                continue;
            }

            // Register a topic
            for (Class<?> c : topicFactoryMap.keySet()) {
                if (c.isAssignableFrom(command.getClass())) {
                    HelpTopic t = topicFactoryMap.get(c).createTopic(command);
                    if (t != null) addTopic(t);
                    continue outer;
                }
                if (command instanceof PluginCommand && c.isAssignableFrom(((PluginCommand) command).getExecutor().getClass())) {
                    HelpTopic t = topicFactoryMap.get(c).createTopic(command);
                    if (t != null) addTopic(t);
                    continue outer;
                }
            }
            addTopic(new GenericCommandHelpTopic(command));
        }

        // todo: help on alias topics

        // todo: alias sub-index

        // Initialize plugin-level sub-topics
        Map<String, Set<HelpTopic>> pluginIndexes = new HashMap<>();
        fillPluginIndexes(pluginIndexes, server.getCommandMap().getCommands());

        for (Map.Entry<String, Set<HelpTopic>> entry : pluginIndexes.entrySet()) {
            addTopic(new IndexHelpTopic(entry.getKey(), "All commands for " + entry.getKey(), null, entry.getValue(), "Below is a list of all " + entry.getKey() + " commands:"));
        }

        // todo: amended topics from help.yml
    }

    private void fillPluginIndexes(Map<String, Set<HelpTopic>> pluginIndexes, Collection<? extends Command> commands) {
        for (Command command : commands) {
            String pluginName = getCommandPluginName(command);
            if (pluginName != null) {
                HelpTopic topic = getHelpTopic("/" + command.getLabel());
                if (topic != null) {
                    if (!pluginIndexes.containsKey(pluginName)) {
                        pluginIndexes.put(pluginName, new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance())); //keep things in topic order
                    }
                    pluginIndexes.get(pluginName).add(topic);
                }
            }
        }
    }

    private String getCommandPluginName(Command command) {
        if (command instanceof BukkitCommand || command instanceof VanillaCommand) {
            return "Bukkit";
        }
        if (command instanceof PluginIdentifiableCommand) {
            return ((PluginIdentifiableCommand) command).getPlugin().getName();
        }
        return null;
    }

    private boolean commandInIgnoredPlugin(Command command, Set<String> ignoredPlugins) {
        if ((command instanceof BukkitCommand || command instanceof VanillaCommand) && ignoredPlugins.contains("Bukkit")) {
            return true;
        }
        if (command instanceof PluginIdentifiableCommand && ignoredPlugins.contains(((PluginIdentifiableCommand) command).getPlugin().getName())) {
            return true;
        }
        return false;
    }

}
