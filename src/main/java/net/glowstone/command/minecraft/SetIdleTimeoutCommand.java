package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class SetIdleTimeoutCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SetIdleTimeoutCommand() {
        super("setidletimeout", "Sets the time before idle players are kicked from the server.",
            "/setidletimeout <Minutes until kick>", Collections.emptyList());
        setPermission("minecraft.command.setidletimeout");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        final String stringTimeout = args[0];
        int timeout;

        try {
            timeout = Integer.parseInt(stringTimeout);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "'" + stringTimeout + "' is not a valid number");
            return false;
        }

        if (timeout <= 0) {
            sender.sendMessage(ChatColor.RED + "The number you have entered (" + timeout
                + ") is too small, it must be at least 1");
            return false;
        }

        GlowServerProvider.getServer().setIdleTimeout(timeout);
        sender.sendMessage("Successfully set the idle timeout to " + timeout + " minutes.");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
