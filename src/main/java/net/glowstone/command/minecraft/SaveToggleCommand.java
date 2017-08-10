package net.glowstone.command.minecraft;

import net.glowstone.GlowServer;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;
import java.util.List;

public class SaveToggleCommand extends VanillaCommand {
    private final boolean on;

    public SaveToggleCommand(boolean on) {
        super(on ? "save-on" : "save-off", GlowServer.lang.getString("command.minecraft." + (on ? "saveon" : "saveoff") + ".description"), GlowServer.lang.getString("command.minecraft." + (on ? "saveon" : "saveoff") + ".usage"), Collections.emptyList());
        this.on = on;
        setPermission(on ? "minecraft.command.save-on" : "minecraft.command.save-off");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        for (World world : sender.getServer().getWorlds()) {
            world.setAutoSave(on);
        }
        sender.sendMessage(GlowServer.lang.getString(sender, "command.minecraft." + (on ? "saveon" : "saveoff") + ".toggle"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
