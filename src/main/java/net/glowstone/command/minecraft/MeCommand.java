package net.glowstone.command.minecraft;

import java.util.Arrays;
import java.util.Collections;
import net.glowstone.command.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class MeCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public MeCommand() {
        super("me", "Displays a message about yourself.", "/me <action ...>",
            Collections.emptyList());
        setPermission("minecraft.command.me");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        final StringBuilder message = new StringBuilder("* ").append(CommandUtils.getName(sender));
        Arrays.stream(args).forEach(parameter -> message.append(" ").append(parameter));

        Bukkit.broadcastMessage(message.toString());

        return true;
    }
}
