package net.glowstone.command.minecraft;

import com.google.common.net.InetAddresses;
import net.glowstone.ServerProvider;
import net.glowstone.i18n.LocalizedStringImpl;
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
            new LocalizedStringImpl("pardon-ip.invalid", commandMessages.getResourceBundle())
                .sendInColor(ChatColor.RED, sender, ip);
            return false;
        }
        ServerProvider.getServer().unbanIP(ip);
        new LocalizedStringImpl("pardon-ip.done", commandMessages.getResourceBundle())
            .send(sender, ip);
        return true;
    }
}
