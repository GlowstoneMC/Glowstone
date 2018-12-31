package net.glowstone.command.minecraft;

import com.google.common.net.InetAddresses;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanIpCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public BanIpCommand() {
        super("ban-ip", Collections.emptyList());
        setPermission("minecraft.command.ban-ip"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        final ResourceBundle resourceBundle = messages.getResourceBundle();
        if (args.length > 0) {
            String target = null;
            if (InetAddresses.isInetAddress(args[0])) {
                target = args[0];
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    target = player.getAddress().getAddress().getHostAddress();
                }
            }
            if (target != null) {
                if (args.length == 1) {
                    Bukkit.getBanList(BanList.Type.IP).addBan(target, null, null, null);
                } else {
                    StringBuilder reason = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                    Bukkit.getBanList(BanList.Type.IP)
                        .addBan(target, reason.toString(), null, null);
                }
                new LocalizedStringImpl("ban-ip.done", resourceBundle).send(sender, target);
                return true;
            }
            new LocalizedStringImpl("ban-ip.invalid", resourceBundle).send(sender);
            return false;
        }
        sendUsageMessage(sender, messages);
        return false;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            super.tabComplete(sender, alias, args);
        }
        return Collections.emptyList();
    }
}
