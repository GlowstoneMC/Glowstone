package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.ServerProvider;
import net.glowstone.command.GameModeUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

public class DefaultGameModeCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public DefaultGameModeCommand() {
        super("defaultgamemode");
        setPermission("minecraft.command.defaultgamemode"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }

        if (args.length == 0) {
            sendUsageMessage(sender, messages);
            return false;
        }

        final String inputMode = args[0];
        final ResourceBundle bundle = messages.getResourceBundle();
        final GameMode gamemode = GameModeUtils.build(inputMode, messages.getLocale());

        if (gamemode == null) {
            new LocalizedStringImpl("defaultgamemode.unknown", bundle)
                    .sendInColor(ChatColor.RED, sender, inputMode);
            return false;
        }

        ServerProvider.getServer().setDefaultGameMode(gamemode);
        new LocalizedStringImpl("defaultgamemode.done", bundle).send(sender,
                ChatColor.GRAY + "" + ChatColor.ITALIC
                        + GameModeUtils.prettyPrint(gamemode, messages.getLocale()));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        if (args.length == 1) {
            return GameModeUtils.partialMatchingGameModes(args[0], getBundle(sender).getLocale());
        } else {
            return Collections.emptyList();
        }
    }

}
