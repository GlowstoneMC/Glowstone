package net.glowstone.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;

/**
 * A built-in command to demonstrate all chat colors.
 */
public class ColorCommand extends BukkitCommand {
    
    public ColorCommand(String name) {
        super(name, "Display all colors.", "/colors", Arrays.<String>asList());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        String[] names = new String[] {
            "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA",
            "DARK_RED", "DARK_PURPLE", "GOLD", "GRAY",
            "DARK_GRAY", "BLUE", "GREEN", "AQUA",
            "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
        };
        for (int i = 0; i < 16; i += 2) {
            sender.sendMessage(ChatColor.values()[i] + names[i] + ChatColor.WHITE + " -- " + ChatColor.values()[i + 1] + names[i + 1]);
        }
        return true;
    }

}
