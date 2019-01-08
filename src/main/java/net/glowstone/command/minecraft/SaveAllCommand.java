package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class SaveAllCommand extends GlowVanillaCommand {

    public SaveAllCommand() {
        super("save-all");
        setPermission("minecraft.command.save-all");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        ResourceBundle bundle = commandMessages.getResourceBundle();
        new LocalizedStringImpl("save-all.starting", bundle).send(sender);
        LocalizedStringImpl doneWorld = new LocalizedStringImpl("save-all.done.world", bundle);
        for (World world : sender.getServer().getWorlds()) {
            world.save();
            doneWorld.send(sender, world.getName());
        }
        new LocalizedStringImpl("save-all.done", bundle).send(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
            throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
