package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.List;

public class SaveAllCommand extends VanillaCommand {
    public SaveAllCommand() {
        super("save-all", GlowServer.lang.getString("command.minecraft.saveall.description"), GlowServer.lang.getString("command.minecraft.saveall.usage"), Collections.emptyList());
        setPermission("minecraft.command.save-all");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.saveall.saving"));
        for (World world : sender.getServer().getWorlds()) {
            world.save();
            sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.saveall.saved", world.getName()));
        }
        sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft.saveall.done"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
