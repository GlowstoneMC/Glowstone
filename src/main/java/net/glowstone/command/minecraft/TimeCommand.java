package net.glowstone.command.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowCommandWithSubcommands;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NonNls;

public class TimeCommand extends GlowCommandWithSubcommands {

    @NonNls
    private static final String ROOT_COMMAND_NAME = "time";
    @NonNls
    private static final ImmutableList<String> TIMES = ImmutableList.of("day", "night");
    @NonNls
    private static final ImmutableList<String> QUERIES =
            ImmutableList.of("gametime", "daytime", "day");
    private static final Subcommand ADD = new Subcommand(ROOT_COMMAND_NAME, "add") {
            @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            int mod;
                try {
                mod = Integer.valueOf(args[1]);
            } catch (NumberFormatException ex) {
                commandMessages.getGeneric(GenericMessage.NAN).send(sender, args[1]);
                return false;
            }
            sender.sendMessage("Added " + mod + " to the time");
            World world = CommandUtils.getWorld(sender);
            world.setTime(world.getTime() + mod);
            return true;
        }
    };
    private static final Subcommand SET = new Subcommand(ROOT_COMMAND_NAME, "set") {

        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            int mod;
            String value = args[1].toLowerCase(Locale.ENGLISH);
            if (value.equals("day")) { // NON-NLS
                mod = 1000;
            } else if (value.equals("night")) { // NON-NLS
                mod = 13000;
            } else {
                try {
                    mod = Integer.valueOf(value);
                } catch (NumberFormatException ex) {
                    commandMessages.getGeneric(GenericMessage.NAN).send(sender, value);
                    return false;
                }
            }
            CommandUtils.getWorld(sender).setTime(mod);
            sender.sendMessage("Set the time to " + mod);
            return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
                throws IllegalArgumentException {
            return (List) StringUtil
                    .copyPartialMatches(args[1], TIMES, new ArrayList(TIMES.size()));
        }
    };
    private static final Subcommand QUERY = new Subcommand(ROOT_COMMAND_NAME, "query") {

        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            World world = CommandUtils.getWorld(sender);
            String output;
            switch (args[1].toLowerCase()) {
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
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
                throws IllegalArgumentException {
            return (List) StringUtil
                    .copyPartialMatches(args[1], QUERIES, new ArrayList(QUERIES.size()));
        }
    };

    /**
     * Creates the instance for this command.
     */
    public TimeCommand() {
        super("time", Arrays.asList(ADD, SET, QUERY));
        setPermission("minecraft.command.time"); // NON-NLS
    }
}
