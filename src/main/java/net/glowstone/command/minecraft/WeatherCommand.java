package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WeatherCommand extends VanillaCommand {

    private static final List<String> WEATHER = Arrays.asList("clear", "rain", "thunder");

    public WeatherCommand() {
        super("weather", I.tr("command.minecraft.weather.description"), I.tr("command.minecraft.weather.usage"), Collections.emptyList());
        setPermission("minecraft.command.weather");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length == 0 || args.length > 2 || !WEATHER.contains(args[0])) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.weather.usage")));
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        if (world == null) {
            return false;
        }
        String type = args[0];
        Integer duration = null;
        if (args.length == 2) {
            try {
                duration = Integer.valueOf(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.nan", args[1]));
                return false;
            }
            if (duration < 1) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.weather.toosmall", args[1]));
                return false;
            } else if (duration > 1000000) {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.weather.toobig", args[1]));
                return false;
            }
        }
        if (type.equals("clear")) {
            world.setThundering(false);
            world.setStorm(false);
            sender.sendMessage(I.tr(sender, "command.minecraft.weather.clear"));
        } else if (type.equals("rain")) {
            world.setThundering(false);
            world.setStorm(true);
            sender.sendMessage(I.tr(sender, "command.minecraft.weather.clear"));
        } else if (type.equals("thunder")) {
            world.setThundering(true);
            world.setStorm(true);
            sender.sendMessage(I.tr(sender, "command.minecraft.weather.clear"));
        }
        if (duration != null) {
            world.setWeatherDuration(duration * 20);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], WEATHER, new ArrayList(WEATHER.size()));
        }
        return Collections.emptyList();
    }
}
