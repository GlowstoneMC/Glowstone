package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class TimeCommand extends GlowVanillaCommand {

    private static final List<String> SUBCOMMANDS = Arrays.asList("set", "add", "query");
    private static final List<String> TIMES = Arrays.asList("day", "night", "noon", "midnight");

    /**
     * Creates the instance for this command.
     */
    public TimeCommand() {
        super("time");
        setPermission("minecraft.command.time"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length != 2) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        String subcommand = args[0];
        String value = args[1];
        int mod;
        boolean add;
        if (subcommand.equals("set")) {
            add = false;
            if (value.equals("day")) {
                mod = 1000;
            } else if (value.equals("night")) {
                mod = 13000;
            } else if (value.equals("noon")) {
                mod = 6000;
            } else if (value.equals("midnight")) {
                mod = 18000;
            } else {
                try {
                    mod = Integer.valueOf(value);
                } catch (NumberFormatException ex) {
                    commandMessages.getGeneric(GenericMessage.NAN).send(sender, value);
                    return false;
                }
            }
            sender.sendMessage("Set the time to " + mod);
        } else if (subcommand.equals("add")) {
            add = true;
            try {
                mod = Integer.valueOf(value);
            } catch (NumberFormatException ex) {
                commandMessages.getGeneric(GenericMessage.NAN).send(sender, value);
                return false;
            }
            sender.sendMessage("Added " + mod + " to the time");
        } else if (subcommand.equals("query")) {
            String output;
            switch (value.toLowerCase()) {
                case "gametime":
                    output = "The time is " + world.getTime();
                    break;
                case "daytime":
                    output = "The time of day is " + world.getTime() % 24000;
                    break;
                case "day":
                    output = "The day is " + world.getTime() / 24000;
                    break;
                default:
                    sendUsageMessage(sender, commandMessages);
                    return false;
            }
            sender.sendMessage(output);
            // TODO: Set the success count for command blocks
            return true;
        } else {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        world.setTime(add ? world.getTime() + mod : mod);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil
                    .copyPartialMatches(args[0], SUBCOMMANDS, new ArrayList<>(SUBCOMMANDS.size()));
        }
        if (args.length == 2 && args[0].equals("set")) {
            return StringUtil
                    .copyPartialMatches(args[1], TIMES, new ArrayList<>(TIMES.size()));
        }
        return Collections.emptyList();
    }
}
