package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

public class SaveToggleCommand extends VanillaCommand {

    private final boolean on;

    /**
     * Creates the instance for the {@code /save-on} or {@code /save-off} command.
     *
     * @param on true for {@code /save-on}; false for {@code /save-off}
     */
    public SaveToggleCommand(boolean on) {
        super(on ? "save-on" : "save-off",
            on ? "Enables automatic server saves." : "Disables automatic sever saves.",
            on ? "/save-on" : "/save-off", Collections.emptyList());
        this.on = on;
        setPermission(on ? "minecraft.command.save-on" : "minecraft.command.save-off");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }
        for (World world : sender.getServer().getWorlds()) {
            world.setAutoSave(on);
        }
        sender.sendMessage("Turned " + (on ? "on" : "off") + " world auto-saving");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
