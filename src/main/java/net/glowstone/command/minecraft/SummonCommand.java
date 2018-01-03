package net.glowstone.command.minecraft;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.glowstone.GlowWorld;
import net.glowstone.command.CommandUtils;
import net.glowstone.entity.CustomEntityDescriptor;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.Summonable;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SummonCommand extends VanillaCommand {

    /**
     * Creates the instance for this command.
     */
    public SummonCommand() {
        super("summon", "Summons an entity.", "/summon <EntityName> [x] [y] [z] [dataTag]",
            Collections.<String>emptyList());
        setPermission("minecraft.command.summon");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(
                "This command can only be executed by a player or via command blocks.");
            return true;
        }

        Location location = CommandUtils.getLocation(sender);

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        if (args.length >= 4) {
            location = CommandUtils.getLocation(location, args[1], args[2], args[3]);
        }
        location.setYaw(0.0f);
        location.setPitch(0.0f);

        CompoundTag tag = null;
        if (args.length >= 5) {
            String data = String
                .join(" ", new ArrayList<>(Arrays.asList(args)).subList(4, args.length));
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
            entity = (GlowEntity) location.getWorld()
                .spawnEntity(location, EntityType.fromName(entityName));
        } else {
            Class<? extends GlowEntity> clazz = EntityRegistry.getCustomEntityDescriptor(entityName)
                .getEntityClass();
            entity = ((GlowWorld) location.getWorld())
                .spawn(location, clazz, CreatureSpawnEvent.SpawnReason.CUSTOM);
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
        if (entityType == null && EntityRegistry.isCustomEntityRegistered(type)) {
            CustomEntityDescriptor descriptor = EntityRegistry.getCustomEntityDescriptor(type);
            if (!descriptor.isSummonable()) {
                sender.sendMessage(ChatColor.RED + "The entity '" + entityType.getName()
                    + "' cannot be summoned.");
                return false;
            }
            return true;
        }
        if (entityType != null && EntityRegistry.getEntity(entityType) == null) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "The entity '" + entityType.getName()
                    + "' is not implemented yet.");
            }
            return false;
        } else if (entityType != null && (!entityType.isSpawnable() && !Summonable.class
            .isAssignableFrom(EntityRegistry.getEntity(entityType)))) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "The entity '" + entityType.getName()
                    + "' cannot be summoned.");
            }
            return false;
        } else if (entityType == null && !EntityRegistry.isCustomEntityRegistered(type)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "The entity '" + type + "' does not exist.");
            }
            return false;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        throws IllegalArgumentException {
        Preconditions.checkNotNull(sender, "Sender cannot be null");
        Preconditions.checkNotNull(args, "Arguments cannot be null");
        Preconditions.checkNotNull(alias, "Alias cannot be null");
        if (args.length == 1) {
            String arg = args[0];
            ArrayList<String> completion = new ArrayList<>();
            for (EntityType type : EntityType.values()) {
                if (checkSummon(null, type.getName()) && type.getName().toLowerCase()
                    .startsWith(arg)) {
                    completion.add(type.getName());
                }
            }
            EntityRegistry.getRegisteredCustomEntities().forEach((d) -> {
                if (d.getId().toLowerCase().startsWith(arg)) {
                    completion.add(d.getId().toLowerCase());
                }
            });
            return completion;
        } else {
            return Collections.emptyList();
        }
    }
}
