package net.glowstone.command.minecraft;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.state.BlockStateData;
import net.glowstone.block.state.InvalidBlockStateException;
import net.glowstone.block.state.StateSerialization;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.mojangson.Mojangson;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestForBlockCommand extends VanillaCommand {
    public TestForBlockCommand() {
        super("testforblock",
                "Tests for a certain block at a given location",
                "/testforblock <x> <y> <z> <block> [dataValue|state] [dataTag]",
                Collections.emptyList());
        setPermission("minecraft.command.testforblock");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }
        String itemName = args[3];
        if (!itemName.startsWith("minecraft:")) {
            itemName = "minecraft:" + itemName;
        }
        Material type = ItemIds.getItem(itemName);
        Location location = CommandUtils.getLocation(CommandUtils.getLocation(sender), args[0], args[1], args[2]);
        GlowBlock block = (GlowBlock) location.getBlock();
        if (block.getType() != type) {
            sender.sendMessage(ChatColor.RED + "The block at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                    " is " + ItemIds.getName(block.getType()) + " (expected: " + ItemIds.getName(type) + ")");
            return false;
        }
        if (args.length > 4) {
            String state = args[4];
            try {
                int data = Integer.valueOf(state);
                if (data != -1 && block.getData() != data) {
                    sender.sendMessage(ChatColor.RED + "The block at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                            " had the data value of " + block.getData() + " (expected: " + data + ")");
                    return false;
                }
            } catch (NumberFormatException numberEx) {
                // It's not a data value
                try {
                    BlockStateData data = StateSerialization.parse(block.getType(), state);
                    boolean matches = StateSerialization.matches(block.getType(), block.getState().getData(), data);
                    if (!matches) {
                        // TODO: Print the actual state of the block
                        sender.sendMessage(ChatColor.RED + "The block at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                                " did not match the expected state of " + state);
                        return false;
                    }
                } catch (InvalidBlockStateException e) {
                    sender.sendMessage(ChatColor.RED + e.getMessage());
                    return false;
                }
            }
        }
        if (args.length > 5 && block.getBlockEntity() != null) {
            String dataTag = String.join(" ", new ArrayList<>(Arrays.asList(args)).subList(5, args.length));
            try {
                CompoundTag tag = Mojangson.parseCompound(dataTag);
                CompoundTag blockTag = new CompoundTag();
                block.getBlockEntity().saveNbt(blockTag);
                if (!tag.matches(blockTag)) {
                    sender.sendMessage(ChatColor.RED + "The block at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                            " did not have the required NBT keys");
                    return false;
                }
            } catch (MojangsonParseException e) {
                sender.sendMessage(ChatColor.RED + "Invalid Data Tag: " + e.getMessage());
                return false;
            }
        }
        // All is well
        sendSuccess(sender, location);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 4) {
            String start = args[3];
            if (!"minecraft:".startsWith(start)) {
                int colon = start.indexOf(':');
                start = "minecraft:" + start.substring(colon == -1 ? 0 : (colon + 1));
            }
            return (List) StringUtil.copyPartialMatches(start, ItemIds.getIds(), new ArrayList(ItemIds.getIds().size()));
        }
        return Collections.emptyList();
    }

    private void sendSuccess(CommandSender sender, Location location) {
        sender.sendMessage("Successfully found the block at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
    }
}
