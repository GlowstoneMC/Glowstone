package net.glowstone.block.blocktype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import net.glowstone.EventFactory;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.ItemTable;
import net.glowstone.block.MaterialValueManager;
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
import org.bukkit.event.block.BlockPistonRetractEvent;
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

    private void performMovement(BlockFace direction,
        List<Block> blocksToMove, List<Block> blocksToBreak) {

        blocksToMove.sort((a, b) -> {
            switch (direction) {
                case NORTH:
                    return a.getZ() - b.getZ();
                case SOUTH:
                    return b.getZ() - a.getZ();
                case EAST:
                    return b.getX() - a.getX();
                case WEST:
                    return a.getX() - b.getX();
                case UP:
                    return b.getY() - a.getY();
                case DOWN:
                    return a.getY() - b.getY();
                default:
                    return 0;
            }
        });

        for (Block block : blocksToBreak) {
            breakBlock((GlowBlock) block);
        }

        for (Block block : blocksToMove) {
            setType(block.getRelative(direction), block.getTypeId(), block.getData());

            // Need to do this to remove pulled blocks
            setType(block, 0, 0);
        }
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
            List<Block> blocksToMove = new ArrayList<>();
            List<Block> blocksToBreak = new ArrayList<>();

            boolean allowMovement = addBlock(me, pistonBlockFace,
                me.getRelative(pistonBlockFace), pistonBlockFace.getOppositeFace(),
                blocksToMove, blocksToBreak);

            if (!allowMovement) {
                return;
            }

            BlockPistonExtendEvent event = EventFactory.getInstance().callEvent(
                    new BlockPistonExtendEvent(me, blocksToMove, pistonBlockFace)
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

            performMovement(pistonBlockFace, blocksToMove, blocksToBreak);

            // set piston head block when extended
            setType(me.getRelative(pistonBlockFace), 34, sticky ? me.getData() | 0x08 : rawFace);

            return;
        }

        if (!isPistonExtended(me)) {
            return;
        }

        List<Block> blocksToMove = new ArrayList<>();
        List<Block> blocksToBreak = new ArrayList<>();

        if (sticky) {
            addBlock(me, pistonBlockFace.getOppositeFace(),
                me.getRelative(pistonBlockFace, 2), pistonBlockFace.getOppositeFace(),
                blocksToMove, blocksToBreak);
        }

        BlockPistonRetractEvent event = EventFactory.getInstance().callEvent(
            new BlockPistonRetractEvent(me, blocksToMove, pistonBlockFace)
        );

        if (event.isCancelled()) {
            return;
        }

        world.getRawPlayers().stream().filter(player -> player.canSeeChunk(chunkKey))
            .forEach(player -> player.getSession().send(message));
        world.playSound(me.getLocation(), Sound.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5f,
            0.75f);

        // normal state for piston
        setType(me, me.getTypeId(), me.getData() & ~0x08);

        if (sticky && blocksToMove.size() > 0) {
            performMovement(pistonBlockFace.getOppositeFace(), blocksToMove, blocksToBreak);
        } else {
            // remove piston head
            me.getRelative(pistonBlockFace).setTypeIdAndData(0, (byte) 0, true);
        }
    }

    private static final BlockFace[] ADJACENT_FACES = new BlockFace[] {
        BlockFace.NORTH, BlockFace.EAST,
        BlockFace.SOUTH, BlockFace.WEST,
        BlockFace.UP, BlockFace.DOWN
    };

    private boolean addBlock(GlowBlock piston, BlockFace movementDirection,
        GlowBlock block, BlockFace ignoredFace,
        List<Block> blocksToMove, List<Block> blocksToBreak) {

        boolean isPushing = (movementDirection == ignoredFace.getOppositeFace());
        MaterialValueManager.ValueCollection materialValues = block.getMaterialValues();
        PistonMoveBehavior moveBehavior =
            isPushing ? materialValues.getPistonPushBehavior() : materialValues.getPistonPullBehavior();

        if (block.isEmpty()) {
            return true;
        }

        if (blocksToMove.size() >= PUSH_LIMIT) {
            return false;
        }

        if (isPushing) {
            switch (moveBehavior) {
                case MOVE_STICKY:
                case MOVE:
                    blocksToMove.add(block);
                    break;
                case BREAK:
                    blocksToBreak.add(block);
                    return true;
                case DONT_MOVE:
                    return false;
                default:
                    return true;
            }
        } else {
            switch (moveBehavior) {
                case MOVE_STICKY:
                case MOVE:
                    blocksToMove.add(block);
                    break;
                case BREAK:
                case DONT_MOVE:
                    return true;
                default:
                    return true;
            }
        }

        if (moveBehavior == PistonMoveBehavior.MOVE_STICKY) {
            boolean allowMovement = true;
            for (BlockFace face : ADJACENT_FACES) {
                GlowBlock nextBlock = block.getRelative(face);

                if (nextBlock.getLocation().equals(piston.getLocation())) {
                    continue;
                }

                if (face == ignoredFace || blocksToMove.contains(nextBlock)) {
                    continue;
                }

                allowMovement = addBlock(piston, movementDirection,
                    block.getRelative(face), face.getOppositeFace(), blocksToMove, blocksToBreak);

                if (!allowMovement) {
                    break;
                }
            }
            return allowMovement;
        } else if (movementDirection != ignoredFace) {
            GlowBlock nextBlock = block.getRelative(movementDirection);
            if (nextBlock.getLocation().equals(piston.getLocation())) {
                return false;
            }

            return addBlock(piston, movementDirection,
                nextBlock, movementDirection.getOppositeFace(), blocksToMove, blocksToBreak);
        } else {
            return true;
        }
    }

    private void breakBlock(GlowBlock block) {
        if (block.isEmpty()) {
            return;
        }

        GlowWorld world = (GlowWorld) block.getWorld();

        Collection<ItemStack> drops = new ArrayList<>();

        BlockType blockType = ItemTable.instance().getBlock(block.getType());
        if (world.getGameRuleMap().getBoolean("doTileDrops")) {
            drops.addAll(blockType.getMinedDrops(block));
        } else {
            // Container contents is dropped anyways
            if (blockType instanceof BlockContainer) {
                drops.addAll(((BlockContainer) blockType).getContentDrops(block));
            }
        }

        Location location = block.getLocation();
        setType(block, 0, 0);
        if (drops.size() > 0 && !(blockType instanceof BlockLiquid)) {
            drops.stream().forEach((stack) -> block.getWorld().dropItemNaturally(location, stack));
        }
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
