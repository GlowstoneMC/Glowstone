package net.glowstone.command.glowstone;

import com.google.common.base.Preconditions;
import net.glowstone.util.ReflectionProcessor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;

public class GlowstoneCommand extends BukkitCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("about", "eval", "help", "property", "vm");

    public GlowstoneCommand() {
        super("glowstone", "A handful of Glowstone commands for debugging purposes", "/glowstone help", Arrays.asList("gs"));
        setPermission("glowstone.debug");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("about"))) {
            // some info about this Glowstone server
            sender.sendMessage("Information about this server:");
            sender.sendMessage(" - " + ChatColor.GOLD + "Server brand: " + ChatColor.AQUA + Bukkit.getName() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "Server name: " + ChatColor.AQUA + Bukkit.getServerName() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "Glowstone version: " + ChatColor.AQUA + Bukkit.getVersion() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "API version: " + ChatColor.AQUA + Bukkit.getBukkitVersion() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "Players: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "Worlds: " + ChatColor.AQUA + Bukkit.getWorlds().size() + ChatColor.RESET + ".");
            sender.sendMessage(" - " + ChatColor.GOLD + "Plugins: " + ChatColor.AQUA + Bukkit.getPluginManager().getPlugins().length + ChatColor.RESET + ".");

            // thread count
            int threadCount = 0;
            Set<Thread> threads = Thread.getAllStackTraces().keySet();
            for (Thread t : threads) {
                if (t.getThreadGroup() == Thread.currentThread().getThreadGroup()) {
                    threadCount++;
                }
            }
            sender.sendMessage(" - " + ChatColor.GOLD + "Threads: " + ChatColor.AQUA + threadCount + ChatColor.RESET + ".");
            return false;
        }
        if (args[0].equalsIgnoreCase("help")) {
            // some help
            return false;
        }
        if (args[0].equalsIgnoreCase("property")) {
            if (args.length == 1) {
                // list all
                System.getProperties().forEach((key, value) -> sender.sendMessage("Property '" + ChatColor.AQUA + key + ChatColor.RESET + "' = \"" + ChatColor.GOLD + value + ChatColor.RESET + "\"."));
            } else {
                // get a property
                String key = args[1].toLowerCase();
                String value = System.getProperty(key);
                if (value == null) {
                    sender.sendMessage(ChatColor.RED + "Unknown system property '" + key + "'.");
                } else {
                    sender.sendMessage("Property '" + ChatColor.AQUA + key + ChatColor.RESET + "' = \"" + ChatColor.GOLD + value + ChatColor.RESET + "\".");
                }
            }
            return false;
        }
        if (args[0].equalsIgnoreCase("vm")) {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            List<String> arguments = runtimeMxBean.getInputArguments();
            if (arguments.size() == 0) {
                sender.sendMessage("There are no VM arguments.");
            } else {
                sender.sendMessage("Glowstone VM arguments (" + arguments.size() + "):");
                for (String argument : arguments) {
                    sender.sendMessage(" - '" + ChatColor.AQUA + argument + ChatColor.RESET + "'.");
                }
            }

            return false;
        }
        if (args[0].equals("eval")) {
            if (args.length == 1) {
                // no args, send usage
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " eval <eval>");
                return false;
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                builder.append(args[i] + (i == args.length - 1 ? "" : " "));
            }
            ReflectionProcessor processor = new ReflectionProcessor(builder.toString(), sender instanceof Entity ? sender : Bukkit.getServer());
            Object result = processor.process();
            sender.sendMessage(ChatColor.GOLD + "Eval returned: " + (result == null ? ChatColor.RED + "<no value>" : ChatColor.AQUA + result.toString()));
        }
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(args, "Arguments cannot be null");
        Preconditions.checkNotNull(alias, "Alias cannot be null");
        if (args.length == 0) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList(SUBCOMMANDS.size()));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("property")) {
            return (List) StringUtil.copyPartialMatches(args[1], System.getProperties().stringPropertyNames(), new ArrayList(System.getProperties().stringPropertyNames().size()));
        }
        return Collections.emptyList();
    }
}
