package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.i18n.ConsoleMessages;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class DeopCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public DeopCommand() {
        super("deop");
        setPermission("minecraft.command.deop"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        if (args.length != 1) {
            sendUsageMessage(sender, messages);
            return false;
        }
        String name = args[0];
        GlowServer server = (GlowServer) ServerProvider.getServer();
        // asynchronously lookup player
        server.getOfflinePlayerAsync(name).whenCompleteAsync((player, ex) -> {
            if (ex != null) {
                new LocalizedStringImpl("deop.failed", messages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, name, ex.getMessage());
                ConsoleMessages.Error.Command.DEOP_FAILED.log(ex, name);
                return;
            }
            if (player.isOp()) {
                player.setOp(false);
                new LocalizedStringImpl("deop.done", messages.getResourceBundle())
                    .send(sender, name);
            } else {
                new LocalizedStringImpl("deop.not-op", messages.getResourceBundle())
                    .sendInColor(ChatColor.RED, sender, name);
            }
        });
        // todo: asynchronous command callbacks?
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            List<String> operators = new ArrayList<>();
            Bukkit.getOperators().stream().map(OfflinePlayer::getName)
                .filter(Objects::nonNull)
                .forEach(player -> operators.add(player));
            return StringUtil.copyPartialMatches(args[0], operators,
                new ArrayList<>(operators.size()));
        } else if (args.length > 1) {
            return Collections.emptyList();
        }
        return super.tabComplete(sender, alias, args);
    }
}
