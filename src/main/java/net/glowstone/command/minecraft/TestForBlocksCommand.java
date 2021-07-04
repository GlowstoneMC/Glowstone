package net.glowstone.command.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.command.CommandUtils;
import net.glowstone.i18n.LocalizedStringImpl;
import net.glowstone.util.RectangularRegion;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.util.Iterator;
import java.util.Objects;
import java.util.ResourceBundle;

import static net.glowstone.util.RectangularRegion.IterationDirection.FORWARDS;

public class TestForBlocksCommand extends GlowVanillaCommand {
    @RequiredArgsConstructor
    public enum MatchMode {
        ALL("all") {
            @Override
            public boolean isFiltered(GlowBlock fromBlock) {
                return false;
            }
        },
        MASKED("masked") {
            @Override
            public boolean isFiltered(GlowBlock fromBlock) {
                return fromBlock.getType() == Material.AIR;
            }
        };

        @Getter
        private final String commandName;

        /**
         * Returns the MatchMode with a given subcommand name, or null if none match.
         *
         * @param commandName the subcommand name to look up.
         * @return the mask mode.
         */
        public static MatchMode fromCommandName(
                String commandName) {
            for (MatchMode matchMode : values()) {
                if (matchMode.getCommandName().equals(commandName)) {
                    return matchMode;
                }
            }
            return null;
        }

        /**
         * Whether the source region's block is filtered from matching.
         *
         * @param fromBlock The block in the source region.
         * @return true if the block is filtered, false otherwise.
         */
        public abstract boolean isFiltered(GlowBlock fromBlock);

        /**
         * Whether the block from the source region matches the block from the destination region.
         *
         * @param fromBlock The block from the source region.
         * @param toBlock   The block from the destination region.
         * @return true if the blocks match, false otherwise.
         */
        public boolean matches(GlowBlock fromBlock, GlowBlock toBlock) {
            if (isFiltered(fromBlock)) {
                return true;
            }
            CompoundTag fromEntityTag = null;
            if (fromBlock.getBlockEntity() != null) {
                fromEntityTag = new CompoundTag();
                fromBlock.getBlockEntity().saveNbt(fromEntityTag);
            }
            CompoundTag toEntityTag = null;
            if (toBlock.getBlockEntity() != null) {
                toEntityTag = new CompoundTag();
                toBlock.getBlockEntity().saveNbt(fromEntityTag);
            }
            Material fromType = fromBlock.getType();
            Material toType = toBlock.getType();
            byte fromData = fromBlock.getData();
            byte toData = toBlock.getData();
            return Objects.equals(fromType, toType)
                    && Objects.equals(fromData, toData)
                    && Objects.equals(fromEntityTag, toEntityTag);
        }
    }

    /**
     * Creates the instance for this command.
     */
    public TestForBlocksCommand() {
        super("testforblocks");
        setPermission("minecraft.command.testforblocks"); // NON-NLS
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args,
            CommandMessages commandMessages) {
        if (!testPermission(sender, commandMessages.getPermissionMessage())) {
            return true;
        }

        if (args.length < 9) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        if (!CommandUtils.isPhysical(sender)) {
            commandMessages.getGeneric(GenericMessage.NOT_PHYSICAL).send(sender);
            return false;
        }

        GlowWorld world = CommandUtils.getWorld(sender);

        Location parsedFrom1 = CommandUtils.getLocation(sender, args[0], args[1], args[2]);
        Location parsedFrom2 = CommandUtils.getLocation(sender, args[3], args[4], args[5]);
        Location to = CommandUtils.getLocation(sender, args[6], args[7], args[8]);

        MatchMode matchMode = args.length >= 10
                ? MatchMode
                .fromCommandName(args[9]) :
                MatchMode.ALL;

        if (matchMode == null) {
            sendUsageMessage(sender, commandMessages);
            return false;
        }

        RectangularRegion fromRegion = new RectangularRegion(parsedFrom1, parsedFrom2);
        RectangularRegion toRegion = fromRegion.moveTo(to);

        Iterator<Location> fromIterator = fromRegion
                .blockLocations(FORWARDS, FORWARDS, FORWARDS).iterator();
        Iterator<Location> toIterator = toRegion
                .blockLocations(FORWARDS, FORWARDS, FORWARDS).iterator();

        int blocksMatched = 0;

        ResourceBundle bundle = commandMessages.getResourceBundle();
        while (fromIterator.hasNext() && toIterator.hasNext()) {
            Location fromLocation = fromIterator.next();
            Location toLocation = toIterator.next();

            GlowBlock fromBlock = world.getBlockAt(fromLocation);
            GlowBlock toBlock = world.getBlockAt(toLocation);

            if (matchMode.matches(fromBlock, toBlock)) {
                if (!matchMode.isFiltered(fromBlock)) {
                    blocksMatched++;
                }
            } else {
                new LocalizedStringImpl("testforblocks.no-match", bundle)
                        .sendInColor(ChatColor.RED, sender);
                return false;
            }
        }

        new LocalizedStringImpl("testforblocks.done", bundle)
                .send(sender, blocksMatched);

        return true;
    }


}
