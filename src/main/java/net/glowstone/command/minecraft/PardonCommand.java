package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.BanList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PardonCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public PardonCommand() {
        super("pardon");
        setPermission("minecraft.command.pardon"); // NON-NLS
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
        String name = args[0];
        final GlowServer server = (GlowServer) ServerProvider.getServer();
        // asynchronously lookup player
        server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
            if (ex != null) {
                new LocalizedStringImpl("pardon.exception", commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, name, ex.getMessage());
                ex.printStackTrace();
                return;
            }
            BanList banList = server.getBanList(BanList.Type.NAME);
            String exactName = player.getName();
            if (!banList.isBanned(exactName)) {
                new LocalizedStringImpl("pardon.not-banned", commandMessages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, exactName);
                return;
            }
            banList.pardon(exactName);
            new LocalizedStringImpl("pardon.done", commandMessages.getResourceBundle())
                .send(sender, exactName);
        });
        // todo: asynchronous command callbacks?
        return true;
    }
}
