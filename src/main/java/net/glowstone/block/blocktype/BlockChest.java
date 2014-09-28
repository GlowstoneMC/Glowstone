package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEChest;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;

public class BlockChest extends BlockContainer {

    private final boolean isTrapped;

    public BlockChest() {
        this(false);
    }

    public BlockChest(boolean isTrapped) {
        this.isTrapped = isTrapped;
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEChest(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Chest) {
            Chest chest = (Chest) data;
            GlowBlock chestBlock = state.getBlock();

            BlockFace normalFacing = getOppositeBlockFace(player.getLocation(), false);

            Collection<BlockFace> attachedChests = searchChests(chestBlock);
            if (attachedChests.isEmpty()) {
                chest.setFacingDirection(normalFacing);
                state.setData(chest);
                return;
            } else if (attachedChests.size() > 1) {
                GlowServer.logger.warning("Chest placed near two other chests!");
                return;
            }

            BlockFace otherPart = attachedChests.iterator().next();

            GlowBlock otherPartBlock = chestBlock.getRelative(otherPart);
            BlockState otherPartState = otherPartBlock.getState();
            MaterialData otherPartData = otherPartState.getData();

            if (otherPartData instanceof Chest) {
                Chest otherChest = (Chest) otherPartData;
                BlockFace facing = getFacingDirection(normalFacing, otherChest.getFacing(), otherPart, player);

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
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face, Vector clickedLoc) {
        BlockState state = block.getState();
        if (state instanceof org.bukkit.block.Chest) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) state;
            //todo animation etc.
            player.openInventory(chest.getInventory());
            return true;
        }

        GlowServer.logger.warning("Calling blockInteract on BlockChest, but BlockState is " + state);

        return false;
    }

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        Collection<BlockFace> nearChests = searchChests(block);
        if (nearChests.size() > 1) {
            return false;
        }

        return true;
    }

    private Collection<BlockFace> searchChests(GlowBlock block) {
        Collection<BlockFace> chests = new ArrayList<>();

        for (BlockFace face : nearChests) {
            GlowBlock possibleChest = block.getRelative(face);
            if (possibleChest.getType() == (isTrapped ? Material.TRAPPED_CHEST : Material.CHEST)) {
                chests.add(face);
            }
        }

        return chests;
    }

    public BlockFace getAttachedChest(GlowBlock me) {
        Collection<BlockFace> attachedChests = searchChests(me);
        if (attachedChests.isEmpty())
            return null;
        if (attachedChests.size() > 1) {
            GlowServer.logger.warning("Chest may only have one near other chest. Found '" + attachedChests + "' near " + me);
            return null;
        }

        return attachedChests.iterator().next();
    }

    private static final BlockFace[] nearChests = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

    private static BlockFace getFacingDirection(BlockFace myFacing, BlockFace otherFacing, BlockFace connection, GlowPlayer player) {
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
                GlowServer.logger.warning("Can only handle N/O/S/W BlockFaces, getting face: " + connection);
                return BlockFace.NORTH;
        }
    }
}
