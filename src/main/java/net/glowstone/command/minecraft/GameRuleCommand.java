package net.glowstone.command.minecraft;

import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameRuleCommand extends VanillaCommand {
    public GameRuleCommand() {
        super("gamerule", I.tr("command.minecraft.gamerule.description"), I.tr("command.minecraft.gamerule.usage"), Collections.emptyList());
        setPermission("minecraft.command.gamerule");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;
        GlowWorld world = CommandUtils.getWorld(sender);
        if (world == null) return false;
        if (args.length == 0) {
            sender.sendMessage(CommandUtils.prettyPrint(world.getGameRules()));
            return true;
        }
        if (args.length == 1) {
            String gamerule = args[0];
            if (world.getGameRuleMap().isGameRule(gamerule)) {
                String value = world.getGameRuleValue(gamerule);
                if (value == null) value = "-";
                sender.sendMessage(gamerule + " = " + value);
                return true;
            } else {
                sender.sendMessage(I.tr(sender, "command.minecraft.gamerule.missing", gamerule));
                return false;
            }
        }
        String value = StringUtils.join(args, " ", 1, args.length);
        world.setGameRuleValue(args[0], value);
        sender.sendMessage(I.tr(sender, "command.minecraft.gamerule.updated", args[0], value));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        GlowWorld world = CommandUtils.getWorld(sender);
        if (world == null) return Collections.emptyList();
        if (args.length == 1) {
            return (List) StringUtil.copyPartialMatches(args[0], Arrays.asList(world.getGameRules()), new ArrayList(world.getGameRules().length));
        }
        return Collections.emptyList();
    }
}
