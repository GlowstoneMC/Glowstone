package net.glowstone.command.glowstone;

import java.util.Collections;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

/**
 * A built-in command to demonstrate all chat colors.
 */
public class ColorCommand extends BukkitCommand {

    public ColorCommand() {
        super("colors", "Display all colors.", "/colors", Collections.emptyList());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        ChatColor[] values = ChatColor.values();
        for (int i = 0; i < values.length; i += 2) {
            sender.sendMessage(
                values[i] + values[i].name() + ChatColor.WHITE + " -- " + values[i + 1] + values[i
                    + 1].name());
        }
        return true;
    }

}
