package net.glowstone.command.minecraft;

import com.google.common.net.InetAddresses;
import net.glowstone.ServerProvider;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PardonIpCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public PardonIpCommand() {
        super("pardon-ip");
        setPermission("minecraft.command.pardon-ip"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length != 1) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }
        String ip = args[0];
        if (!InetAddresses.isInetAddress(ip)) {
            sender.sendMessage(ChatColor.RED + "You have entered an invalid IP address");
            return false;
        }
        ServerProvider.getServer().unbanIP(ip);
        sender.sendMessage("Unbanned IP address " + ip);
        return true;
    }
}
