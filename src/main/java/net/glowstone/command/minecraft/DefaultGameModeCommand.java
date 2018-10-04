package net.glowstone.command.minecraft;

import static net.glowstone.i18n.GlowstoneMessages.GameMode.NAMES_LOWERCASE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.glowstone.ServerProvider;
import net.glowstone.i18n.GlowstoneMessages;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class DefaultGameModeCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public DefaultGameModeCommand() {
        super("defaultgamemode",
            "Sets the default game mode (creative, survival, etc.) for new players entering a "
                    + "multiplayer server.",
            "/defaultgamemode <mode>", Collections.emptyList());
        setPermission("minecraft.command.defaultgamemode");
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

        final String inputMode = args[0];
        final GameMode gamemode = GameModeUtils.build(inputMode);

        if (gamemode == null) {
            sender.sendMessage(ChatColor.RED + "Unknown mode '" + inputMode + "'.");
            return false;
        }

        ServerProvider.getServer().setDefaultGameMode(gamemode);
        sender.sendMessage(
            "The world's default game mode is now " + ChatColor.GRAY + "" + ChatColor.ITALIC
                + GameModeUtils.prettyPrint(gamemode) + " Mode");

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], NAMES_LOWERCASE,
                new ArrayList(((List<String>) NAMES_LOWERCASE).size()));
        } else {
            return Collections.emptyList();
        }
    }
}
