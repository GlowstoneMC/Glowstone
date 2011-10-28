package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

/**
 * Command to ban players or IP addresses.
 */
public class BanCommand extends GlowCommand {

    public BanCommand(GlowServer server) {
        super(server, "ban", "Manage player and ip bans", "[-ip] add|remove|check name|ip");
    }
    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        int mod = 0;
        boolean ip = false;
        if (!checkArgs(sender, args, 2, 3)) return false;
        if (args[0].equalsIgnoreCase("-ip")) {
            ip = true; mod = 1;
        }
        String option = args[mod];
        String target = args[1 + mod];
        Player player = server.getPlayer(target);
        String senderName = (sender instanceof Player? ((Player) sender).getDisplayName(): "Console");
        if (option.equalsIgnoreCase("add")) {
            if (ip) {
                if (!checkPermission(sender, "addip")) return false;
                String newTarget = target;
                if (player != null) newTarget = player.getAddress().getAddress().getHostAddress();
                server.getBanManager().setIpBanned(newTarget, true);
            } else {
                if (!checkPermission(sender, "add")) return false;
                server.getBanManager().setBanned(target, true);
            }
            if (player != null) player.kickPlayer("You have been " + (ip ? "ip banned" : "banned") + " by " + senderName);
            server.broadcastMessage(ChatColor.RED + target + " has been banned by " + senderName);
        } else if (option.equalsIgnoreCase("remove")) {
           if (ip) {
               if (!checkPermission(sender, "removeip")) return false;
                server.getBanManager().setIpBanned(target, false);
            } else {
               if (!checkPermission(sender, "remove")) return false;
                server.getBanManager().setBanned(target, false);
            }
            sender.sendMessage(ChatColor.RED + target + " was unbanned.");
        } else if (option.equalsIgnoreCase("check")) {
            boolean banned;
            if (!checkPermission(sender, "check")) return false;
            if (ip) {
                banned = server.getBanManager().isIpBanned(target);
            } else {
                banned = server.getBanManager().isBanned(target);
            }
            sender.sendMessage(ChatColor.RED + target + (banned ? " is" : " is not") + " banned.");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Invalid option. Usage: " + usageMessage);
            return false;
        }
        return true;
    }

    @Override
    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> perms = new HashSet<Permission>();
        perms.add(new Permission(prefix + ".add", "Allow users to add name bans to the ban list"));
        perms.add(new Permission(prefix + ".addip", "Allow users to add ip bans to the ban list"));
        perms.add(new Permission(prefix + ".remove", "Allow users to remove name bans from the ban list"));
        perms.add(new Permission(prefix + ".removeip", "Allow users to remove ip bans from the ban list"));
        perms.add(new Permission(prefix + ".check", "Allow users to check if a name or ip is banned"));
        return perms;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
