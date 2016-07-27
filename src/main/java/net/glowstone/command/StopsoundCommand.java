package net.glowstone.command;

import com.flowpowered.network.util.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.PluginMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collections;

public class StopsoundCommand extends BukkitCommand {

    public StopsoundCommand() {
        super("stopsound", "Stops sounds for a player.", "/stopsound <player> [source] [sound]", Collections.emptyList());
        setPermission("glowstone.command.stopsound");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return false;
        }

        Player player = Bukkit.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "The player '" + args[0] + "' is not online, or does not exist.");
            return false;
        }

        String source = "", sound = "";
        String message = "Stopped all sounds for player '" + player.getName() + "'.";
        if (args.length > 1) {
            source = args[1];
            message = "Stopped sounds from source " + source + " for player '" + player.getName() + "'.";
        }
        if (args.length > 2) {
            sound = args[2];
            message = "Stopped sound '" + sound + "' for player '" + player.getName() + "'.";
        }

        ByteBuf buffer = Unpooled.buffer();
        try {
            ByteBufUtils.writeUTF8(buffer, source);
            ByteBufUtils.writeUTF8(buffer, sound);
            ((GlowPlayer) player).getSession().send(new PluginMessage("MC|StopSound", buffer.array()));
            buffer.release();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        sender.sendMessage(message);
        return true;
    }
}
