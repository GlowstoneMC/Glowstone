package net.glowstone.command.minecraft;

import java.util.Collections;
import java.util.Iterator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.command.CommandUtils;
import net.glowstone.constants.ItemIds;
import net.glowstone.util.RectangularRegion;
import net.glowstone.util.RectangularRegion.IterationDirection;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.VanillaCommand;


public class CloneCommand extends VanillaCommand {
    @RequiredArgsConstructor
    public enum MaskMode {
        REPLACE("replace"),
        MASKED("masked"),
        FILTERED("filter");

        @Getter
        private final String commandName;

        /**
         * Returns the MaskMode with a given subcommand name, or null if none match.
         *
         * @param commandName the subcommand name to look up.
         * @return the mask mode.
         */
        public static MaskMode fromCommandName(String commandName) {
            for (MaskMode maskMode : values()) {
                if (maskMode.getCommandName().equals(commandName)) {
                    return maskMode;
                }
            }
            return null;
        }
    }

    public enum CloneMode {
        NORMAL("normal", false),
        FORCE("force", true),
        MOVE("move", false);

        @Getter
        private final String commandName;
        @Getter
        private final boolean allowedToOverlap;

        CloneMode(String commandName, boolean allowedToOverlap) {
            this.commandName = commandName;
            this.allowedToOverlap = allowedToOverlap;
        }

        /**
         * Returns the CloneMode with a given subcommand name, or null if none match.
         *
         * @param commandName the subcommand name to look up.
         * @return the clone mode.
         */
        public static CloneMode fromCommandName(String commandName) {
            for (CloneMode cloneMode : values()) {
                if (cloneMode.getCommandName().equals(commandName)) {
                    return cloneMode;
                }
            }
            return null;
        }
    }

    /**
     * Creates the instance for this command.
     */
    public CloneCommand() {
        super(
                "clone",
                "Clones a section of the world.",
                "/clone <x1> <y1> <z1>  <x2> <y2> <z2>  <x> <y> <z> "
                        + "[maskMode] [cloneMode] [tileName] [tileData]",
                Collections.emptyList()
        );
        setPermission("minecraft.command.clone");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender)) {
            return false;
        }

        if (args.length < 9) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        if (!CommandUtils.isPhysical(sender)) {
            sender.sendMessage("This command may only be executed by physical objects");
            return false;
        }

        GlowWorld world = CommandUtils.getWorld(sender);

        Location parsedFrom1 = CommandUtils.getLocation(sender, args[0], args[1], args[2]);
        Location parsedFrom2 = CommandUtils.getLocation(sender, args[3], args[4], args[5]);
        Location to = CommandUtils.getLocation(sender, args[6], args[7], args[8]);

        MaskMode maskMode = args.length >= 10
                ? MaskMode.fromCommandName(args[9]) : MaskMode.REPLACE;
        CloneMode cloneMode = args.length >= 11
                ? CloneMode.fromCommandName(args[10]) : CloneMode.NORMAL;

        // TODO: Investigate what happens when maskMode or cloneMode are invalid (thus, null).
        if (maskMode == null || cloneMode == null) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        BlockFilter blockFilter;

        switch (maskMode) {
            case REPLACE:
                blockFilter = new ReplaceBlockFilter();
                break;

            case MASKED:
                blockFilter = new MaskedBlockFilter();
                break;

            case FILTERED:
                if (args.length >= 12) {
                    Material blockType = ItemIds.getItem(args[11]);
                    if (args.length >= 13) {
                        Byte data;
                        try {
                            data = Byte.parseByte(args[12]);
                        } catch (NumberFormatException ignored) {
                            data = null;
                        }
                        if (data == null || 0 > data || data > 15) {
                            sender.sendMessage(ChatColor.RED
                                    + "Filtered block data not a number between 0 and 15, "
                                    + "inclusive.");
                            return false;
                        } else {
                            blockFilter = new FilteredWithDataBlockFilter(blockType, data);
                        }
                    } else {
                        blockFilter = new FilteredBlockFilter(blockType);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED
                            + "You must specify a block type and, optionally, block data when "
                            + "using the filtered mask mode.");
                    return false;
                }
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
                return false;
        }

        RectangularRegion fromRegion = new RectangularRegion(parsedFrom1, parsedFrom2);
        RectangularRegion toRegion = fromRegion.moveTo(to);
        
        Location lowCorner = fromRegion.getLowCorner();
        Location highCorner = fromRegion.getHighCorner();

        boolean overlaps = between(lowCorner.getBlockX(), highCorner.getBlockX(), to.getBlockX())
                || between(lowCorner.getBlockY(), highCorner.getBlockY(), to.getBlockY())
                || between(lowCorner.getBlockZ(), highCorner.getBlockZ(), to.getBlockZ());

        if (overlaps && !cloneMode.isAllowedToOverlap()) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usageMessage);
            return false;
        }

        int blocksCloned = 0;

        IterationDirection directionX = to.getBlockX() < lowCorner.getBlockX()
                ? IterationDirection.FORWARDS : IterationDirection.BACKWARDS;
        IterationDirection directionY = to.getBlockY() < lowCorner.getBlockY()
                ? IterationDirection.FORWARDS : IterationDirection.BACKWARDS;
        IterationDirection directionZ = to.getBlockZ() < lowCorner.getBlockZ()
                ? IterationDirection.FORWARDS : IterationDirection.BACKWARDS;

        Iterator<Location> fromIterator = fromRegion
                .blockLocations(directionX, directionY, directionZ).iterator();
        Iterator<Location> toIterator = toRegion
                .blockLocations(directionX, directionY, directionZ).iterator();

        while (fromIterator.hasNext() && toIterator.hasNext()) {
            Location fromLocation = fromIterator.next();
            Location toLocation = toIterator.next();

            GlowBlock fromBlock = world.getBlockAt(fromLocation);

            if (blockFilter.shouldClone(fromBlock)) {
                GlowBlock toBlock = world.getBlockAt(toLocation);
                toBlock.setTypeIdAndData(fromBlock.getTypeId(), fromBlock.getData(), false);

                BlockEntity fromEntity = fromBlock.getBlockEntity();
                if (fromEntity != null) {
                    BlockEntity toEntity = toBlock.getChunk()
                            .createEntity(toBlock.getX(), toBlock.getY(), toBlock
                                    .getZ(), toBlock.getTypeId());
                    if (toEntity != null) {
                        CompoundTag entityTag = new CompoundTag();
                        fromEntity.saveNbt(entityTag);
                        toEntity.loadNbt(entityTag);
                    }
                }

                if (cloneMode == CloneMode.MOVE) {
                    fromBlock.setType(Material.AIR, false);
                }

                blocksCloned++;
            }
        }


        if (blocksCloned == 0) {
            sender.sendMessage(ChatColor.RED + "No blocks cloned.");
        } else {
            sender.sendMessage("Cloned " + blocksCloned + " blocks.");
        }
        return true;
    }

    private static boolean between(double low, double high, double n) {
        return low < n && n < high;
    }

    private interface BlockFilter {
        boolean shouldClone(GlowBlock block);
    }

    private static class ReplaceBlockFilter implements BlockFilter {
        @Override
        public boolean shouldClone(GlowBlock block) {
            return true;
        }
    }

    private static class MaskedBlockFilter implements BlockFilter {
        @Override
        public boolean shouldClone(GlowBlock block) {
            return block.getType() != Material.AIR;
        }
    }

    private static class FilteredBlockFilter implements BlockFilter {
        private final Material blockType;

        private FilteredBlockFilter(Material blockType) {
            this.blockType = blockType;
        }

        @Override
        public boolean shouldClone(GlowBlock block) {
            return block.getType() == blockType;
        }
    }

    private static class FilteredWithDataBlockFilter extends FilteredBlockFilter {
        private final byte data;

        private FilteredWithDataBlockFilter(Material blockType, byte data) {
            super(blockType);
            this.data = data;
        }

        @Override
        public boolean shouldClone(GlowBlock block) {
            return super.shouldClone(block) && block.getData() == data;
        }
    }
}
