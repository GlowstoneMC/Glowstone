package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

/**
 * A built-in command to save world data.
 */
public class SaveCommand extends GlowCommand {

    public SaveCommand(GlowServer server) {
        super(server, "save", "Save world data.", "<do|on|off> [world]");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (!checkArgs(sender, args, 1, 2)) return false;
        World world;
        if (args.length == 2) {
            world = server.getWorld(args[1]);
            if (world == null) {
                sender.sendMessage(ChatColor.GRAY + "World " + args[1] + " does not exist.");
                return false;
            }
        } else if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = server.getWorlds().get(0);
        }
        String action = args[0];
        if (action.equals("on")) {
            if (!checkPermission(sender, "on")) return false;
            world.setAutoSave(true);
            tellOps(sender, "Enabling autosave for world " + world.getName());
        } else if (action.equals("off")) {
            if (!checkPermission(sender, "off")) return false;
            world.setAutoSave(false);
            tellOps(sender, "Disabling autosave for world " + world.getName());
        } else if (action.equals("do")){
            if (!checkPermission(sender, "do")) return false;
            world.save();
            tellOps(sender, "Saving data of world " + world.getName());
        } else {
            sender.sendMessage(ChatColor.GRAY + action + " is not a valid action for the save command.");
            return false;
        }
        return true;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }

    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> perms = new HashSet<Permission>();
        perms.add(new Permission(prefix + ".do", "Perform a world save"));
        perms.add(new Permission(prefix + ".on", "Enable world saving"));
        perms.add(new Permission(prefix + ".off", "Disable world saving"));
        return perms;
    }

}
