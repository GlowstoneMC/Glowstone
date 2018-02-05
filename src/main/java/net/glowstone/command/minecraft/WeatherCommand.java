package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class WeatherCommand extends VanillaCommand {

    private static final List<String> WEATHER = Arrays.asList("clear", "rain", "thunder");

    /**
     * Creates the instance for this command.
     */
    public WeatherCommand() {
        super("weather", "Changes the weather in the world.",
            "/weather <clear|rain|thunder> [duration in seconds]", Collections.emptyList());
        setPermission("minecraft.command.weather");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0 || args.length > 2 || !WEATHER.contains(args[0])) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        String type = args[0];
        Integer duration = null;
        if (args.length == 2) {
            try {
                duration = Integer.valueOf(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number");
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
        if (type.equals("clear")) {
            world.setThundering(false);
            world.setStorm(false);
            sender.sendMessage("Changing to clear weather");
        } else if (type.equals("rain")) {
            world.setThundering(false);
            world.setStorm(true);
            sender.sendMessage("Changing to rainy weather");
        } else if (type.equals("thunder")) {
            world.setThundering(true);
            world.setStorm(true);
            sender.sendMessage("Changing to rain and thunder");
        }
        if (duration != null) {
            world.setWeatherDuration(duration * 20);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil
                .copyPartialMatches(args[0], WEATHER, new ArrayList(WEATHER.size()));
        }
        return Collections.emptyList();
    }
}
