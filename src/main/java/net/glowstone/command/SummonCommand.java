package net.glowstone.command;

import net.glowstone.entity.GlowEntity;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SummonCommand extends BukkitCommand {

    public SummonCommand() {
        super("summon", "Summons an entity.", "/summon <EntityName> [x] [y] [z] [dataTag]", Collections.<String>emptyList());
        setPermission("glowstone.command.summon");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("This command can only be executed by a player or via command blocks.");
            return true;
        }

        Location location = null;
        if (sender instanceof Player) {
            location = ((Player) sender).getLocation().clone();
        } else if (sender instanceof BlockCommandSender) {
            location = ((BlockCommandSender) sender).getBlock().getLocation().clone();
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        if (args.length >= 4) {
            double x = this.getCoordinate(sender, location.getX(), args[1]);
            double y = this.getCoordinate(sender, location.getY(), args[2]);
            double z = this.getCoordinate(sender, location.getZ(), args[3]);
            location = new Location(location.getWorld(), x, y, z);
        }

        CompoundTag tag = null;
        if (args.length >= 5) {
            String data = String.join(" ", new ArrayList<String>(Arrays.asList(args)).subList(4, args.length));
            sender.sendMessage(data);
            try {
                tag = Mojangson.parseCompound(data);
            } catch (MojangsonParseException e) {
                sender.sendMessage(ChatColor.RED + "Invalid Data Tag: " + e.getMessage());
            }
        }

        String entityName = args[0];
        EntityType type = EntityType.fromName(entityName);
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "Unknown entity type: " + entityName);
            return true;
        }
        GlowEntity entity = (GlowEntity) location.getWorld().spawnEntity(location, type);
        if (tag != null) {
            EntityStorage.load(entity, tag);
        }

        sender.sendMessage("Object successfully summoned.");
        return true;
    }

    private double getCoordinate(CommandSender sender, double current, String input) {
        return this.getCoordinate(sender, current, input, -30000000, 30000000);
    }

    private double getCoordinate(CommandSender sender, double current, String input, int min, int max) {
        boolean relative = input.startsWith("~");
        double result = relative ? current : 0.0D;
        if (!relative || input.length() > 1) {
            boolean exact = input.contains(".");
            if (relative) {
                input = input.substring(1);
            }

            double testResult = VanillaCommand.getDouble(sender, input);
            if (testResult == -3.0000001E7D) {
                return -3.0000001E7D;
            }

            result += testResult;
            if (!exact && !relative) {
                result += 0.5D;
            }
        }

        if (min != 0 || max != 0) {
            if (result < (double) min) {
                result = -3.0000001E7D;
            }

            if (result > (double) max) {
                result = -3.0000001E7D;
            }
        }

        return result;
    }
}
