package net.glowstone.command.minecraft;

import net.glowstone.i18n.LocalizedStringImpl;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NonNls;

import java.util.Collections;
import java.util.List;

public class SaveToggleCommand extends GlowVanillaCommand {

    @NonNls
    private final String doneMessageKey;
    private final boolean on;

    /**
     * Creates the instance for the {@code /save-on} or {@code /save-off} command.
     *
     * @param on true for {@code /save-on}; false for {@code /save-off}
     */
    public SaveToggleCommand(boolean on) {
        super(on ? "save-on" : "save-off");
        this.on = on;
        doneMessageKey = on ? "save-on.done" : "save-off.done";
        setPermission(on ? "minecraft.command.save-on" : "minecraft.command.save-off"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }
        for (World world : sender.getServer().getWorlds()) {
            world.setAutoSave(on);
        }
        new LocalizedStringImpl(doneMessageKey, commandMessages.getResourceBundle())
                .send(sender);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
