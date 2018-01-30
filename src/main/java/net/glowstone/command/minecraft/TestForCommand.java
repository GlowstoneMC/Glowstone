package net.glowstone.command.minecraft;

import java.util.Arrays;
import java.util.Collections;
import net.glowstone.command.CommandTarget;
import net.glowstone.command.CommandUtils;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Entity;

public class TestForCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public TestForCommand() {
        super("testfor",
            "Tests for a certain target in game",
            "/testfor <player> [dataTag]",
            Collections.emptyList());
        setPermission("minecraft.command.testfor");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        String name = args[0];
        Entity[] entities;
        if (name.startsWith("@")) {
            CommandTarget target = new CommandTarget(sender, name);
            entities = target.getMatched(CommandUtils.getLocation(sender));

            if (entities.length == 0) {
                sender.sendMessage(ChatColor.RED + "Selector '" + name + "' found nothing.");
                return false;
            }
        } else {
            GlowPlayer player = (GlowPlayer) Bukkit.getPlayerExact(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Entity '" + name + "' cannot be found");
                return false;
            } else {
                entities = new Entity[]{player};
            }
        }

        if (args.length >= 2) {
            String data = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            CompoundTag tag;
            try {
                tag = Mojangson.parseCompound(data);
            } catch (MojangsonParseException e) {
                sender.sendMessage(ChatColor.RED + "Invalid Data Tag: " + e.getMessage());
                return false;
            }
            for (Entity entity : entities) {
                if (entity instanceof GlowEntity) {
                    CompoundTag entityTag = new CompoundTag();
                    EntityStorage.save((GlowEntity) entity, entityTag);
                    if (tag.matches(entityTag)) {
                        sender.sendMessage("Found " + CommandUtils.getName(entity));
                    } else {
                        sender.sendMessage(ChatColor.RED + CommandUtils.getName(entity)
                            + " did not match the required data structure");
                    }
                }
            }
        } else {
            for (Entity entity : entities) {
                sender.sendMessage("Found " + CommandUtils.getName(entity));
            }
        }

        // TODO: When command blocks are implemented, this should be updated to output the number of
        // matching entities.
        return true;
    }
}
