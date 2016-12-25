package net.glowstone.command;

import com.google.common.collect.ImmutableList;
import net.glowstone.GlowWorld;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            location = CommandUtils.getLocation(location, args[1], args[2], args[3]);
        }
        if (location == null) {
            return false;
        }
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        CompoundTag tag = null;
        if (args.length >= 5) {
            String data = String.join(" ", new ArrayList<>(Arrays.asList(args)).subList(4, args.length));
            try {
                tag = Mojangson.parseCompound(data);
            } catch (MojangsonParseException e) {
                sender.sendMessage(ChatColor.RED + "Invalid Data Tag: " + e.getMessage());
            }
        }

        String entityName = args[0];
        if (!checkSummon(sender, entityName)) {
            return true;
        }
        GlowEntity entity;
        if (EntityType.fromName(entityName) != null) {
            entity = (GlowEntity) location.getWorld().spawnEntity(location, EntityType.fromName(entityName));
        } else {
            Class<? extends GlowEntity> clazz = EntityRegistry.getCustomEntityDescriptor(entityName).getEntityClass();
            entity = ((GlowWorld) location.getWorld()).spawn(location, clazz, CreatureSpawnEvent.SpawnReason.CUSTOM);
        }
        if (tag != null) {
            EntityStorage.load(entity, tag);
        }

        sender.sendMessage("Object successfully summoned.");
        return true;
    }

    private boolean checkSummon(CommandSender sender, String type) {
        if (type == null) {
            return false;
        }
        EntityType entityType = EntityType.fromName(type);
        if (sender != null) sender.sendMessage(type + " == null: " + (entityType == null) + "!");
        if (entityType != null && !EntityType.fromName(type).isSpawnable()) {
            if (sender != null)
                sender.sendMessage(ChatColor.RED + "The entity '" + EntityType.fromName(type).getName() + "' cannot be summoned.");
            return false;
        }
        if (entityType != null && EntityRegistry.getEntity(entityType) == null) {
            if (sender != null)
                sender.sendMessage(ChatColor.RED + "The entity '" + entityType.getName() + "' is not implemented yet.");
            return false;
        } else if (entityType == null && !EntityRegistry.isCustomEntityRegistered(type)) {
            if (sender != null)
                sender.sendMessage(ChatColor.RED + "The entity '" + type + "' does not exist.");
            return false;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null", new Object[0]);
        Validate.notNull(args, "Arguments cannot be null", new Object[0]);
        Validate.notNull(alias, "Alias cannot be null", new Object[0]);
        if (args.length == 1) {
            String arg = args[0];
            ArrayList<String> completion = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (checkSummon(null, type.getName()) && StringUtils.startsWithIgnoreCase(type.getName(), arg)) {
                    completion.add(type.getName());
                }
            }
            EntityRegistry.getRegisteredCustomEntities().forEach((d) -> {
                if (StringUtils.startsWithIgnoreCase(d.getId(), arg))
                    completion.add(d.getId().toLowerCase());
            });
            return completion;
        } else {
            return ImmutableList.of();
        }
    }
}
