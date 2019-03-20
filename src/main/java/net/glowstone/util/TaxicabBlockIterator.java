package net.glowstone.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class TaxicabBlockIterator implements Iterator<Block> {

    private static final BlockFace[] VALID_FACES = new BlockFace[]{BlockFace.DOWN, BlockFace.UP,
        BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

    private final Queue<Object> pendingAnalysis = new LinkedList<>();
    private final Queue<Block> nextValidBlocks = new LinkedList<>();
    private final Set<Block> usedBlocks = new HashSet<>();
    private int currentDistance = 1;
    private int validBlockCount;

    private int maxDistance = Integer.MAX_VALUE;
    private int maxBlocks = Integer.MAX_VALUE;
    private Predicate<Block> predicate;

    /**
     * Creates an instance.
     *
     * @param origin the origin to start iterating around
     */
    public TaxicabBlockIterator(Block origin) {
        pendingAnalysis.add(origin);
        pendingAnalysis.add(DistanceMarker.INSTANCE);
        usedBlocks.add(origin);
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    public void setPredicate(Predicate<Block> predicate) {
        this.predicate = predicate;
    }

    private boolean isValid(Block block) {
        return predicate == null || predicate.test(block);
    }

    @Override
    public boolean hasNext() {
        if (validBlockCount >= maxBlocks) {
            return false;
        }

        // Keep going till the valid block queue contains something, we reach the distance limit,
        // or we empty the pending analysis queue.
        // Note that the pending analysis queue will always contain at least one element: the end
        // of distance marker.
        while (nextValidBlocks.isEmpty() && currentDistance <= maxDistance
                && pendingAnalysis.size() >= 2) {
            Object object = pendingAnalysis.remove();

            // If we find the end of distance marker, we'll increase the distance, and then we'll
            // re-add it to the end.
            if (object == DistanceMarker.INSTANCE) {
                pendingAnalysis.add(object);
                currentDistance++;
                continue;
            }

            // If it wasn't the EoD marker, it must be a block. We'll look now for valid blocks
            // around it.
            Block block = (Block) object;
            for (BlockFace face : VALID_FACES) {
                Block near = block.getRelative(face);

                // Only analyse the block if we haven't checked it yet.
                if (usedBlocks.add(near) && isValid(near)) {
                    nextValidBlocks.add(near);
                    pendingAnalysis.add(near);
                }
            }
        }

        return !nextValidBlocks.isEmpty();
    }

    @Override
    public Block next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        validBlockCount++;
        return nextValidBlocks.remove();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private static final class DistanceMarker {

        public static final DistanceMarker INSTANCE = new DistanceMarker();

    }
}
