package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.List;

public class SetIdleTimeoutCommand extends VanillaCommand {

    public SetIdleTimeoutCommand() {
        super("setidletimeout", I.tr("command.minecraft.setidletimeout.description"), I.tr("command.minecraft.setidletimeout.usage"), Collections.emptyList());
        setPermission("minecraft.command.setidletimeout");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        if (args.length == 0) {
            sender.sendMessage(I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.setidletimeout.usage")));
            return false;
        }

        final String stringTimeout = args[0];
        int timeout;

        try {
            timeout = Integer.parseInt(stringTimeout);
        } catch (NumberFormatException ex) {
            sender.sendMessage(I.tr(sender, "command.generic.nan", stringTimeout));
            return false;
        }

        if (timeout <= 0) {
            sender.sendMessage(I.tr(sender, "command.minecraft.setidletimeout.toosmall", timeout));
            return false;
        }

        Bukkit.getServer().setIdleTimeout(timeout);
        sender.sendMessage(I.tr(sender, "command.minecraft.setidletimeout.success", timeout));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
