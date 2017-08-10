package net.glowstone.command.minecraft;

import net.glowstone.util.lang.I;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.List;

public class SaveAllCommand extends VanillaCommand {
    public SaveAllCommand() {
        super("save-all", I.tr("command.minecraft.save-all.description"), "/save-all", Collections.emptyList());
        setPermission("minecraft.command.save-all");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        sender.sendMessage(I.tr(sender, "command.minecraft.save-all.saving"));
        for (World world : sender.getServer().getWorlds()) {
            world.save();
            sender.sendMessage(I.tr(sender, "command.minecraft.save-all.saved", world.getName()));
        }
        sender.sendMessage(I.tr(sender, "command.minecraft.save-all.done"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
