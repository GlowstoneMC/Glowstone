package net.glowstone.command.minecraft;

import net.glowstone.command.CommandUtils;
import net.glowstone.util.lang.I;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;

public class SetWorldSpawnCommand extends VanillaCommand {
    public SetWorldSpawnCommand() {
        super("setworldspawn", I.tr("command.minecraft.setworldspawn.description"), I.tr("command.minecraft.setworldspawn.usage"), Collections.emptyList());
        setPermission("minecraft.command.setworldspawn");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) return false;

        Location spawnLocation;

        final World world = CommandUtils.getWorld(sender);

        if (world == null) {
            return false;
        }

        if (args.length == 0) { // Get the player current location
            if (CommandUtils.isPhysical(sender)) {
                spawnLocation = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
            } else {
                sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.setworldspawn.default"));
                return false;
            }
        } else if (args.length >= 3) { // manage arguments
            final Location senderLocation;

            // Get the sender coordinates if relative is used
            if (args[0].startsWith("~") || args[1].startsWith("~") || args[2].startsWith("~")) {
                if (!CommandUtils.isPhysical(sender)) {
                    sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.setworldspawn.relative"));
                    return false;
                } else {
                    senderLocation = sender instanceof Entity ? ((Entity) sender).getLocation() : ((BlockCommandSender) sender).getBlock().getLocation();
                }
            } else { // Otherwise, the current location can be set to 0/0/0 (since it's absolute)
                senderLocation = new Location(world, 0, 0, 0);
            }

            spawnLocation = CommandUtils.getLocation(senderLocation, args[0], args[1], args[2]);
        } else {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.generic.usage", I.tr(sender, "command.minecraft.setworldspawn.usage")));
            return false;
        }

        if (spawnLocation.getBlockY() < 0) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.setworldspawn.ymin", spawnLocation.getBlockY()));
            return false;
        } else if (spawnLocation.getBlockY() > world.getMaxHeight()) {
            sender.sendMessage(ChatColor.RED + I.tr(sender, "command.minecraft.setworldspawn.ymax", spawnLocation.getBlockY(), world.getMaxHeight()));
            return false;
        }

        world.setSpawnLocation(spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ());
        sender.sendMessage(I.tr(sender, "command.minecraft.setworldspawn.set", spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ()));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
