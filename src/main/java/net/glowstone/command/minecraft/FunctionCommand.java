package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.data.CommandFunction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;

public class FunctionCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public FunctionCommand() {
        super("function", "Execute a function",
            "/function <name> [if <selector>|unless <selector>]", Collections.emptyList());
        setPermission("minecraft.command.function");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        if (args.length == 0 || args.length == 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        Location location = CommandUtils.getLocation(sender);
        String functionName = args[0];
        Map<String, CommandFunction> functions = world.getFunctions();
        if (!functions.containsKey(functionName)) {
            sender.sendMessage(ChatColor.RED + "Unknown function '" + functionName + "'");
            return false;
        }
        CommandFunction function = functions.get(functionName);
        if (args.length > 2) {
            String condition = args[1].toLowerCase();
            CommandTarget target = new CommandTarget(sender, args[2]);
            Entity[] matched = target.getMatched(location);
            if (condition.equals("if")) {
                if (matched.length == 0) {
                    sender.sendMessage(
                        "Skipped execution of function '" + function.getFullName() + "'");
                    return false;
                }
            } else if (condition.equals("unless")) {
                if (matched.length > 0) {
                    sender.sendMessage(
                        "Skipped execution of function '" + function.getFullName() + "'");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
            }
        }
        function.execute(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            GlowWorld world = CommandUtils.getWorld(sender);
            return StringUtil.copyPartialMatches(args[0], world.getFunctions().keySet(),
                new ArrayList<>(world.getFunctions().size()));
        }
        return super.tabComplete(sender, alias, args);
    }
}
