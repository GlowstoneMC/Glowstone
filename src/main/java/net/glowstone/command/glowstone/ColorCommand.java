package net.glowstone.command.glowstone;

import net.glowstone.command.minecraft.GlowVanillaCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * A built-in command to demonstrate all chat colors.
 */
public class ColorCommand extends GlowVanillaCommand {

    public ColorCommand() {
        super("colors");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        ChatColor[] values = ChatColor.values();
        for (int i = 0; i < values.length; i += 2) {
            sender.sendMessage(
                values[i] + values[i].name() + ChatColor.WHITE + " -- " + values[i + 1] + values[i
                    + 1].name());
        }
        return true;
    }

}
