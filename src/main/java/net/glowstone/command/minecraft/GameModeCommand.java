package net.glowstone.command.minecraft;

import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.command.GameModeUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.ResourceBundle;

public class GameModeCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public GameModeCommand() {
        super("gamemode");
        setPermission("minecraft.command.gamemode"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
                           CommandMessages messages) {
        if (!testPermission(sender, messages.getPermissionMessage())) {
            return true;
        }
        if (args.length == 0 || args.length == 1 && !(sender instanceof Player)) {
            sendUsageMessage(sender, messages);
            return false;
        }
        final ResourceBundle bundle = messages.getResourceBundle();
        String gm = args[0];
        GameMode gamemode = GameModeUtils.build(gm, messages.getLocale());
        if (gamemode == null) {
            new LocalizedStringImpl("gamemode.unknown", bundle)
                .sendInColor(ChatColor.RED, sender, gm);
            return false;
        }
        if (args.length == 1) {
            // self
            Player player = (Player) sender;
            updateGameMode(sender, player, gamemode, bundle);
            return true;
        }
        String name = args[1];
        if (name.startsWith("@") && name.length() >= 2 && CommandUtils.isPhysical(sender)) {
            Location location = CommandUtils.getLocation(sender);
            CommandTarget target = new CommandTarget(sender, name);
            Entity[] matched = target.getMatched(location);
            for (Entity entity : matched) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    updateGameMode(sender, player, gamemode, bundle);
                }
            }
        } else {
            Player player = Bukkit.getPlayerExact(name);
            if (player == null) {
                messages.getGeneric(GenericMessage.OFFLINE)
                    .sendInColor(ChatColor.RED, sender, name);
            } else {
                updateGameMode(sender, player, gamemode, bundle);
            }
        }
        return true;
    }

    private void updateGameMode(
        CommandSender sender, Player who, GameMode gameMode, ResourceBundle bundle) {
        String gameModeName = GameModeUtils.prettyPrint(gameMode, bundle.getLocale());
        who.setGameMode(gameMode);
        if (!sender.equals(who)) {
            new LocalizedStringImpl("gamemode.done", bundle)
                .send(sender, who.getDisplayName(), gameModeName);
            bundle = getBundle(who); // switch to target's locale for gamemode.done.to-you
        }
        new LocalizedStringImpl("gamemode.done.to-you", bundle).send(who, gameModeName);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        if (args.length == 1) {
            return GameModeUtils.partialMatchingGameModes(args[0], getBundle(sender).getLocale());
        }
        return super.tabComplete(sender, alias, args);
    }
}
