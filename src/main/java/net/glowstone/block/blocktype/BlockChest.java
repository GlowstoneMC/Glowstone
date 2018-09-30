package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.Collection;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.ChestEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.i18n.ConsoleMessages;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockChest extends BlockContainer {

    private final boolean isTrapped;

    public BlockChest() {
        this(false);
    }

    public BlockChest(boolean isTrapped) {
        this.isTrapped = isTrapped;
    }

    private static BlockFace getFacingDirection(BlockFace myFacing, BlockFace otherFacing,
        BlockFace connection, GlowPlayer player) {
        if (connection != myFacing && connection != myFacing.getOppositeFace()) {
            return myFacing;
        }

        if (connection != otherFacing && connection != otherFacing.getOppositeFace()) {
            return otherFacing;
        }

        float yaw = player.getLocation().getYaw() % 360;
        yaw = yaw < 0 ? yaw + 360 : yaw;

        switch (connection) {
            case NORTH:
            case SOUTH:
                return yaw < 180 ? BlockFace.EAST : BlockFace.WEST;
            case EAST:
            case WEST:
                return yaw > 90 && yaw < 270 ? BlockFace.SOUTH : BlockFace.NORTH;
            default:
                ConsoleMessages.Warn.Block.Chest.FACING.log(connection);
                return BlockFace.NORTH;
        }
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new ChestEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Chest) {
            Chest chest = (Chest) data;
            GlowBlock chestBlock = state.getBlock();

            BlockFace normalFacing = getOppositeBlockFace(player.getLocation(), false);

            Collection<BlockFace> attachedChests = searchChests(chestBlock);
            switch (attachedChests.size()) {
                case 0:
                    chest.setFacingDirection(normalFacing);
                    state.setData(chest);
                    return;
                case 1:
                    break;
                default:
                    ConsoleMessages.Warn.Block.Chest.TRIPLE_MIDDLE.log();
                    return;
            }
            BlockFace otherPart = attachedChests.iterator().next();

            GlowBlock otherPartBlock = chestBlock.getRelative(otherPart);

            if (getAttachedChest(otherPartBlock) != null) {
                ConsoleMessages.Warn.Block.Chest.TRIPLE_END.log();
                return;
            }

            BlockState otherPartState = otherPartBlock.getState();
            MaterialData otherPartData = otherPartState.getData();

            if (otherPartData instanceof Chest) {
                Chest otherChest = (Chest) otherPartData;
                BlockFace facing = getFacingDirection(normalFacing, otherChest.getFacing(),
                    otherPart, player);

                chest.setFacingDirection(facing);
                state.setData(chest);

                otherChest.setFacingDirection(facing);
                otherPartState.setData(otherChest);
                otherPartState.update();
            } else {
                warnMaterialData(Chest.class, otherPartData);
            }
        } else {
            warnMaterialData(Chest.class, data);
        }
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        BlockState state = block.getState();
        if (state instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) state;
            player.openInventory(chest.getInventory());
            player.incrementStatistic(Statistic.CHEST_OPENED);
            return true;
        }

        ConsoleMessages.Warn.Block.Chest.INTERACT_WRONG_CLASS.log(state);

        return false;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Collection<BlockFace> nearChests = searchChests(block);

        if (nearChests.size() == 1) {
            GlowBlock otherPartBlock = block.getRelative(nearChests.iterator().next());

            if (getAttachedChest(otherPartBlock) != null) {
                return false;
            }
        }
        return nearChests.size() <= 1;

    }

    private Collection<BlockFace> searchChests(GlowBlock block) {
        Collection<BlockFace> chests = new ArrayList<>();

        for (BlockFace face : SIDES) {
            GlowBlock possibleChest = block.getRelative(face);
            if (possibleChest.getType() == (isTrapped ? Material.TRAPPED_CHEST : Material.CHEST)) {
                chests.add(face);
            }
        }

        return chests;
    }

    /**
     * Get the other half of a chest, or null if the given chest isn't part of a double chest.
     * @param me a chest block
     * @return the other half of the double chest if {@code me} is part of one; null otherwise
     */
    public BlockFace getAttachedChest(GlowBlock me) {
        Collection<BlockFace> attachedChests = searchChests(me);
        if (attachedChests.isEmpty()) {
            return null;
        }
        if (attachedChests.size() > 1) {
            ConsoleMessages.Warn.Block.Chest.TRIPLE_ALREADY.log(attachedChests, me);
            return null;
        }

        return attachedChests.iterator().next();
    }
}
