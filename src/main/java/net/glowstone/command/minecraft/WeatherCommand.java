package net.glowstone.command.minecraft;

import java.util.Arrays;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GlowCommandWithSubcommands;
import net.glowstone.util.TickUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NonNls;

public class WeatherCommand extends GlowCommandWithSubcommands {

    @NonNls
    private static final String ROOT_COMMAND_NAME = "weather";

    private static class WeatherSubcommand extends Subcommand {
        private final boolean thunder;
        private final boolean downpour;
        private final String doneKey;

        WeatherSubcommand(String name, boolean thunder, boolean downpour) {
            super(ROOT_COMMAND_NAME, name);
            this.thunder = thunder;
            this.downpour = downpour;
            doneKey = keyPrefix + ".done";
        }

        @Override
        protected boolean execute(CommandSender sender, String label, String[] args,
                CommandMessages commandMessages) {
            GlowWorld world = CommandUtils.getWorld(sender);
            String type = args[0];
            Integer duration = null;
            if (args.length == 2) {
                try {
                    duration = Integer.valueOf(args[1]);
                } catch (NumberFormatException ex) {
                    commandMessages.getGeneric(GenericMessage.NAN).send(sender, args[1]);
                    return false;
                }
                if (duration < 1) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + args[1]
                            + ") is too small, it must be at least 1");
                    return false;
                } else if (duration > 1000000) {
                    sender.sendMessage(ChatColor.RED + "The number you have entered (" + args[1]
                            + ") is too big, it must be at most 1000000");
                    return false;
                }
            }
            world.setThundering(thunder);
            world.setStorm(downpour);
            if (duration != null) {
                world.setWeatherDuration(TickUtil.secondsToTicks(duration));
            }
            return true;
        }
    }

    /**
     * Creates the instance for this command.
     */
    public WeatherCommand() {
        super(ROOT_COMMAND_NAME, Arrays.asList(
                new WeatherSubcommand("clear", false, false),
                new WeatherSubcommand("rain", false, true),
                new WeatherSubcommand("thunder", true, true)));
        setPermission("minecraft.command.weather"); // NON-NLS
    }
}
