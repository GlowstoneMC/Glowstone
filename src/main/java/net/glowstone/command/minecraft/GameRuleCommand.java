package net.glowstone.command.minecraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class GameRuleCommand extends GlowVanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public GameRuleCommand() {
        super("gamerule");
        setPermission("minecraft.command.gamerule"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        GlowWorld world = CommandUtils.getWorld(sender);
        if (args.length == 0) {
            sender.sendMessage(commandMessages.joinList(world.getGameRules()));
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
                new LocalizedStringImpl("gamerule.unknown", commandMessages.getResourceBundle())
                        .sendInColor(ChatColor.RED, sender, gamerule);
                return false;
            }
        }
        String value = StringUtils.join(args, " ", 1, args.length);
        world.setGameRuleValue(args[0], value);
        // TODO: Should we use the actual value, in case the type conversion was messy?
        new LocalizedStringImpl("gamerule.done", commandMessages.getResourceBundle())
                .send(sender, args[0], value);
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
