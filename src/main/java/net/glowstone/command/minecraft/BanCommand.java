package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BanCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public BanCommand() {
        super("ban", Collections.emptyList());
        setPermission("minecraft.command.ban");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            ResourceBundle resourceBundle, CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        if (args.length > 0) {
            String name = args[0];
            GlowServer server = (GlowServer) ServerProvider.getServer();
            // asynchronously lookup player
            server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
                if (ex != null) {
                    new LocalizedStringImpl("ban.exception", resourceBundle)
                            .sendInColor(sender, ChatColor.RED, name, ex.getMessage());
                    return;
                }
                if (player == null) {
                    new LocalizedStringImpl("ban.no-such-player", resourceBundle)
                            .sendInColor(sender, ChatColor.RED, name);
                    return;
                }
                if (args.length == 1) {
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(),
                            null, null, null);
                } else {
                    StringBuilder reason = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        reason.append(args[i]).append(" ");
                    }
                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(),
                            reason.toString(), null, null);
                }
                new LocalizedStringImpl("ban.done", resourceBundle)
                        .send(sender, player.getName());
            });
            // todo: asynchronous command callbacks?
            return true;
        }
        sendUsageMessage(sender, resourceBundle);
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
