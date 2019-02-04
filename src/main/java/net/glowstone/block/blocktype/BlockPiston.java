package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.PistonMoveBehavior;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.BlockActionMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.PistonBaseMaterial;

public class BlockPiston extends BlockDirectional {

    private static final int PUSH_LIMIT = 12;
    /**
     * The piston is either non-sticky (default), or has a sticky behavior.
     *
     * @return true if the piston has a sticky base
     */
    @Getter
    private final boolean sticky;

    /** Creates the basic (non-sticky) piston block type. */
    public BlockPiston() {
        this(false);
    }

    /**
     * Creates a piston block type.
     * @param sticky true for the sticky-piston type; false for the basic piston type
     */
    public BlockPiston(boolean sticky) {
        super(false);
        this.sticky = sticky;

        if (sticky) {
            setDrops(new ItemStack(Material.PISTON_STICKY_BASE));
        } else {
            setDrops(new ItemStack(Material.PISTON_BASE));
        }
    }

    @Override
    public void blockDestroy(GlowPlayer player, GlowBlock block, BlockFace face) {
        if (block.getType() == Material.PISTON_BASE) {
            // break piston extension if extended
            if (isPistonExtended(block)) {
                block.getRelative(((PistonBaseMaterial) block.getState().getData()).getFacing())
                    .setType(Material.AIR);
            }
        }

        // TODO: handle breaking of piston extension
    }

    @Override
    public void onRedstoneUpdate(GlowBlock me) {
        PistonBaseMaterial piston = (PistonBaseMaterial) me.getState().getData();
        BlockFace pistonBlockFace = piston.getFacing();
        int rawFace = BlockDirectional.getRawFace(pistonBlockFace);
        BlockActionMessage message = new BlockActionMessage(me.getX(), me.getY(), me.getZ(),
            me.isBlockIndirectlyPowered() ? 0 : 1, rawFace, me.getTypeId());

        GlowChunk chunk = me.getChunk();
        GlowChunk.Key chunkKey = GlowChunk.Key.of(chunk.getX(), chunk.getZ());
        GlowWorld world = me.getWorld();

        if (me.isBlockIndirectlyPowered() && !isPistonExtended(me)) {
            List<Block> blocks = new ArrayList<>();

            // get all blocks to be pushed by piston
            // add 2 to push limit to compensate for i starting at 1 and also to get the block after
            // the push limit
            for (int i = 1; i < PUSH_LIMIT + 2; i++) {
                Block block = me.getRelative(pistonBlockFace, i);

                if (block.getType() == Material.AIR) {
                    break;
                }

                PistonMoveBehavior pushBehavior = ((GlowBlock) block).getMaterialValues().getPistonPushBehavior();
                if (pushBehavior == PistonMoveBehavior.BREAK) {
                    break;
                }
                if (pushBehavior == PistonMoveBehavior.DONT_MOVE) {
                    return;
                }

                // if block after push limit is not air then do not push
                if (i == PUSH_LIMIT + 1) {
                    return;
                }

                blocks.add(block);
            }

            BlockPistonExtendEvent event = EventFactory.getInstance().callEvent(
                    new BlockPistonExtendEvent(me, blocks, pistonBlockFace)
            );

            if (event.isCancelled()) {
                return;
            }

            world.getRawPlayers().stream().filter(player -> player.canSeeChunk(chunkKey))
                .forEach(player -> player.getSession().send(message));
            world.playSound(me.getLocation(), Sound.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5f,
                0.75f);

            // extended state for piston base
            me.setData((byte) (me.getData() | 0x08));

            GlowBlock lastBlock = me.getRelative(pistonBlockFace, blocks.size() + 1);
            if (lastBlock.getMaterialValues().getPistonPushBehavior() == PistonMoveBehavior.BREAK) {
                // breakNaturally causes client desync, so we have to do it manually
                Collection<ItemStack> drops = ItemTable.instance().getBlock(lastBlock.getType()).getMinedDrops(lastBlock);
                Location location = lastBlock.getLocation();

                setType(lastBlock, 0, 0);

                drops.stream().forEach(stack -> lastBlock.getWorld().dropItemNaturally(location, stack));
            }

            // TODO Apply physics so gravity-affected stuff falls properly
            for (int i = blocks.size() - 1; i >= 0; i--) {
                Block block = blocks.get(i);

                setType(block.getRelative(pistonBlockFace), block.getTypeId(), block.getData());
            }

            // set piston head block when extended
            setType(me.getRelative(pistonBlockFace), 34, sticky ? me.getData() | 0x08 : rawFace);

            return;
        }

        if (!isPistonExtended(me)) {
            return;
        }

        world.getRawPlayers().stream().filter(player -> player.canSeeChunk(chunkKey))
            .forEach(player -> player.getSession().send(message));
        world.playSound(me.getLocation(), Sound.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f,
            0.75f);

        // normal state for piston
        setType(me, me.getTypeId(), me.getData() & ~0x08);

        if (sticky) {
            Block block = me.getRelative(pistonBlockFace, 2);
            Block relativeBlock = me.getRelative(pistonBlockFace);

            if (block.isEmpty()) {
                relativeBlock.setTypeIdAndData(0, (byte) 0, true);
                return;
            }

            PistonMoveBehavior pullBehavior = ((GlowBlock) block).getMaterialValues().getPistonPullBehavior();
            if (pullBehavior == PistonMoveBehavior.MOVE) {
                // TODO Apply physics so gravity-affected stuff falls properly
                setType(relativeBlock, block.getTypeId(), block.getData());
                setType(block, 0, 0);
            } else {
                relativeBlock.setTypeIdAndData(0, (byte) 0, true);
            }

            return;
        }

        // remove piston head after retracting
        setType(me.getRelative(pistonBlockFace), 0, 0);
    }

    private boolean isPistonExtended(Block block) {
        // TODO: check direction of piston_extension to make sure that the extension is attached to
        // piston
        Block pistonHead = block
            .getRelative(((PistonBaseMaterial) block.getState().getData()).getFacing());
        return pistonHead.getType() == Material.PISTON_EXTENSION;
    }

    // update block server side without sending block change packets
    private void setType(Block block, int type, int data) {
        World world = block.getWorld();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        GlowChunk chunk = (GlowChunk) world.getChunkAt(block);
        chunk.setType(x & 0xf, z & 0xf, y, type);
        chunk.setMetaData(x & 0xf, z & 0xf, y, data);
    }
}
