package net.glowstone.command;

import net.glowstone.GlowServer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.HashSet;
import java.util.Set;

public class ReloadCommand extends GlowCommand {

    public ReloadCommand(GlowServer server) {
        super(server, "reload", "Reloads the server or portions of the server", "all/*|aliases", "rl");
    }

    @Override
    public boolean run(CommandSender sender, String commandLabel, String[] args) {
        if (args.length < 1 || args[0].matches("([Aa]ll|\\*)")) {
            if (!checkPermission(sender, "all")) return false;
            tellOps(sender, "Reloading server");
            server.reload();
        } else if (args[0].matches("[Aa]liases")) {
            if (!checkPermission(sender, "aliases")) return false;
            tellOps(sender, "Reloading command aliases");
            server.reloadCommandAliases();
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Unknown option!");
            return false;
        }
        sender.sendMessage(ChatColor.GREEN + "Reload complete.");
        return true;
    }

    @Override
    public Set<Permission> registerPermissions(String prefix) {
        Set<Permission> ret = new HashSet<Permission>();
        ret.add(new Permission(prefix + ".all", "Gives permission for full server reloads"));
        ret.add(new Permission(prefix + ".aliases", "Gives permission to reload server command aliases"));
        return ret;
    }

    @Override
    public PermissionDefault getPermissionDefault() {
        return PermissionDefault.OP;
    }
}
