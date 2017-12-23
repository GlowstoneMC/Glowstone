package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

public class GameRuleCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public GameRuleCommand() {
        super("gamerule", "Changes the rules of the server.", "/gamerule [rule] [new value]",
            Collections.emptyList());
        setPermission("minecraft.command.gamerule");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        if (args.length == 0) {
            sender.sendMessage(CommandUtils.prettyPrint(world.getGameRules()));
            return true;
        }
        if (args.length == 1) {
            String gamerule = args[0];
            if (world.getGameRuleMap().isGameRule(gamerule)) {
                String value = world.getGameRuleValue(gamerule);
                if (value == null) {
                    value = "-";
                }
                sender.sendMessage(gamerule + " = " + value);
                return true;
            } else {
                sender.sendMessage(
                    ChatColor.RED + "No game rule called '" + gamerule + "' is available");
                return false;
            }
        }
        String value = StringUtils.join(args, " ", 1, args.length);
        world.setGameRuleValue(args[0], value);
        sender.sendMessage("Game rule " + args[0] + " has been updated to " + value);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        GlowWorld world = CommandUtils.getWorld(sender);
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList(world.getGameRules()),
                new ArrayList<>(world.getGameRules().length));
        }
        return Collections.emptyList();
    }
}
