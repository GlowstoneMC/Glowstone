package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.data.CommandFunction;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.util.StringUtil;

public class FunctionCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public FunctionCommand() {
        super("function");
        setPermission("minecraft.command.function"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0 || args.length == 2) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        Location location = CommandUtils.getLocation(sender);
        String functionName = args[0];
        Map<String, CommandFunction> functions = world.getFunctions();
        if (!functions.containsKey(functionName)) {
            new LocalizedStringImpl("function.unknown", commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, functionName);
            return false;
        }
        CommandFunction function = functions.get(functionName);
        if (args.length > 2) {
            String condition = args[1].toLowerCase(Locale.ENGLISH);
            if (condition.equals("if")) { // NON-NLS
                if (!anyMatch(sender, args[2], location)) {
                    new LocalizedStringImpl("function.skipped",
                            commandMessages.getResourceBundle())
                            .send(sender, function.getFullName());
                    return false;
                }
            } else if (condition.equals("unless")) { // NON-NLS
                if (anyMatch(sender, args[2], location)) {
                    new LocalizedStringImpl("function.skipped",
                            commandMessages.getResourceBundle())
                            .send(sender, function.getFullName());
                    return false;
                }
            } else {
                sendUsageMessage(sender, commandMessages);
                return false;
            }
        }
        function.execute(sender);
        return true;
    }

    private boolean anyMatch(CommandSender sender, String selector, Location location) {
        CommandTarget target = new CommandTarget(sender, selector);
        Entity[] matched = target.getMatched(location);
        return matched.length > 0;
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
