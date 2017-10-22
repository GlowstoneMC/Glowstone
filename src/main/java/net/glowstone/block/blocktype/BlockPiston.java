package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.BlockActionMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.PistonBaseMaterial;

import java.util.ArrayList;
import java.util.List;

public class BlockPiston extends BlockDirectional {
    private static final int PUSH_LIMIT = 12;
    private final boolean sticky;

    public BlockPiston() {
        this(false);
    }

    public BlockPiston(boolean sticky) {
        super(false);
        this.sticky = sticky;

        if (sticky) {
            setDrops(new ItemStack(Material.PISTON_STICKY_BASE));
        } else {
            setDrops(new ItemStack(Material.PISTON_BASE));
        }
    }

    /**
     * The piston is either non-sticky (default), or has a sticky behavior
     *
     * @return true if the piston has a sticky base
     */
    public boolean isSticky() {
        return sticky;
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        if (block.getType() == Material.PISTON_BASE) {
            // break piston extension if extended
            if (isPistonExtended(block)) {
                block.getRelative(((PistonBaseMaterial) block.getState().getData()).getFacing()).setType(Material.AIR);
            }
        }

        // TODO: handle breaking of piston extension
    }

    @Override
    public void onRedstoneUpdate(GlowBlock me) {
        PistonBaseMaterial piston = (PistonBaseMaterial) me.getState().getData();
        BlockFace pistonBlockFace = piston.getFacing();
        PistonDirection pistonDirection = PistonDirection.getByBlockFace(pistonBlockFace);

        BlockActionMessage message = new BlockActionMessage(me.getX(), me.getY(), me.getZ(), me.isBlockIndirectlyPowered() ? 0 : 1, pistonDirection.ordinal(), me.getTypeId());

        if (me.isBlockIndirectlyPowered() && !isPistonExtended(me)) {
            List<Block> blocks = new ArrayList<>();

            // get all blocks to be pushed by piston
            // add 2 to push limit to compensate for i starting at 1 and also to get the block after the push limit
            for (int i = 1; i < PUSH_LIMIT + 2; i++) {
                Block block = me.getRelative(pistonBlockFace, i);

                if (block.getType() == Material.AIR) {
                    break;
                }

                // TODO: handle non-pushable blocks.

                // if block after push limit is not air then do not push
                if (i == PUSH_LIMIT + 1) {
                    return;
                }

                blocks.add(block);
            }

            me.getWorld().getNearbyEntities(me.getLocation(), 20, 20, 20).stream().filter(entity -> entity.getType() == EntityType.PLAYER).forEach(player -> {
                ((GlowPlayer) player).getSession().send(message);
            });

            me.getWorld().playSound(me.getLocation(), Sound.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f, 0.75f);

            // extended state for piston base
            me.setData((byte) (me.getData() | 0x08));

            for (int i = blocks.size() - 1; i >= 0; i--) {
                Block block = blocks.get(i);

                setType(block.getRelative(pistonBlockFace), block.getTypeId(), block.getData());
            }

            // set piston head block when extended
            setType(me.getRelative(pistonBlockFace), 34, sticky ? me.getData() | 0x08 : pistonDirection.ordinal());

            return;
        }

        if (!isPistonExtended(me)) {
            return;
        }

        me.getWorld().getNearbyEntities(me.getLocation(), 20, 20, 20).stream().filter(entity -> entity.getType() == EntityType.PLAYER).forEach(player -> {
            ((GlowPlayer) player).getSession().send(message);
        });

        me.getWorld().playSound(me.getLocation(), Sound.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f, 0.75f);

        // normal state for piston
        setType(me, me.getTypeId(), me.getData() & ~0x08);

        if (sticky) {
            Block block = me.getRelative(pistonBlockFace, 2);
            Block relativeBlock = me.getRelative(pistonBlockFace);

            if (block.isEmpty()) {
                relativeBlock.setTypeIdAndData(0, (byte) 0, true);
                return;
            }

            setType(relativeBlock, block.getTypeId(), block.getData());
            setType(block, 0, 0);

            return;
        }

        // remove piston head after retracting
        setType(me.getRelative(pistonBlockFace), 0, 0);
    }

    private boolean isPistonExtended(Block block) {
        // TODO: check direction of piston_extension to make sure that the extension is attached to piston
        Block pistonHead = block.getRelative(((PistonBaseMaterial) block.getState().getData()).getFacing());
        return pistonHead.getType() == Material.PISTON_EXTENSION;
    }

    // update block server side without sending block change packets
    private void setType(Block block, int type, int data) {
        World world = block.getWorld();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        ((GlowChunk) world.getChunkAt(block)).setType(x & 0xf, z & 0xf, y, type);
        ((GlowChunk) world.getChunkAt(block)).setMetaData(x & 0xf, z & 0xf, y, data);
    }

    public enum PistonDirection {
        DOWN,
        UP,
        NORTH,
        SOUTH,
        WEST,
        EAST;

        public static PistonDirection getByBlockFace(BlockFace face) {
            switch (face) {
                case DOWN:
                    return PistonDirection.DOWN;
                case UP:
                    return PistonDirection.UP;
                case NORTH:
                    return PistonDirection.NORTH;
                case SOUTH:
                    return PistonDirection.SOUTH;
                case WEST:
                    return PistonDirection.WEST;
                case EAST:
                    return PistonDirection.EAST;
            }

            return null;
        }
    }
}
