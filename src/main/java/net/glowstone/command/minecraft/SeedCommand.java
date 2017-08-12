package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;

import java.util.Collections;

public class SeedCommand extends VanillaCommand {
    public SeedCommand() {
        super("seed", I.tr("command.minecraft.seed.description"), "/seed", Collections.emptyList());
        setPermission("minecraft.command.seed");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        final World world = CommandUtils.getWorld(sender);

        if (world == null) {
            return false;
        } else {
            sender.sendMessage(I.tr(sender, "command.minecraft.seed.result", world.getSeed()));
        }

        return true;
    }
}
