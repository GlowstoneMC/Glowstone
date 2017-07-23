package net.glowstone.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.glowstone.chunk.GlowChunk.Key;
import net.glowstone.entity.GlowHangingEntity.HangingFace;
import net.glowstone.entity.objects.GlowPainting;
import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class PaintingSpawner {
    private static final Multimap<Key, Art> artBySpawnOffset = HashMultimap.create();

    private static int MAX_OFFSET_TOP;
    private static int MAX_OFFSET_LEFT;
    private static int MAX_WIDTH;
    private static int MAX_HEIGHT;

    static {
        for (Art art : Art.values()) {
            Key offset = getTopLeftOffset(art);
            artBySpawnOffset.put(offset, art);

            if (MAX_OFFSET_TOP < offset.getZ()) {
                MAX_OFFSET_TOP = offset.getZ();
            }
            if (MAX_OFFSET_LEFT < offset.getX()) {
                MAX_OFFSET_LEFT = offset.getX();
            }

            if (MAX_WIDTH < art.getBlockWidth()) {
                MAX_WIDTH = art.getBlockWidth();
            }

            if (MAX_HEIGHT < art.getBlockHeight()) {
                MAX_HEIGHT = art.getBlockHeight();
            }
        }
    }

    private static Key getTopLeftOffset(Art art) {
        int offsetLeft = Math.max(0, art.getBlockWidth() / 2 - 1);
        int offsetTop = art.getBlockHeight() / 2;
        return new Key(offsetLeft, offsetTop);
    }

    public boolean spawnAt(Location center, BlockFace facing) {
        Art art = getArt(center, facing);

        new GlowPainting(center.getBlock().getRelative(facing).getLocation(), facing).setArtInternal(art);


        return true;
    }

    public boolean spawn(Location center, Art art, BlockFace facing, boolean force) {
        if (!force) {
            Key topLeftOffset = getTopLeftOffset(art);
            Key maxSize = getMaxSize(center, facing, topLeftOffset);
            if (maxSize.getX() >= art.getBlockWidth() && maxSize.getZ() >= art.getBlockHeight()) {
                new GlowPainting(center.getBlock().getRelative(facing).getLocation(), facing).setArtInternal(art);
                return true;
            }
            return false;
        }
        new GlowPainting(center.getBlock().getRelative(facing).getLocation(), facing).setArtInternal(art);
        return true;
    }

    private Art getArt(Location center, BlockFace frontFacing) {
        Key topLeftOffset = getOffset(center.clone(), frontFacing, BlockFace.UP, MAX_OFFSET_LEFT, MAX_OFFSET_TOP);

        Key maxSize = getMaxSize(center, frontFacing, topLeftOffset);
        Collection<Art> matchingArt = getMatchingArt(topLeftOffset, maxSize);
        System.out.println(matchingArt.size());
        return matchingArt.iterator().next();
    }

    private Key getMaxSize(Location center, BlockFace frontFacing, Key topLeftOffset) {
        BlockFace widthFacing = HangingFace.values()[(HangingFace.getByBlockFace(frontFacing).ordinal() + 1) % 4].getBlockFace();
        Location topLeftCorner = center.clone();
        topLeftCorner.add(widthFacing.getModX() * topLeftOffset.getX(), topLeftOffset.getZ(), widthFacing.getModZ() * topLeftOffset.getX());
        return getOffset(topLeftCorner, frontFacing, BlockFace.DOWN, MAX_WIDTH, MAX_HEIGHT);
    }

    private Key getOffset(Location center, BlockFace frontFacing, BlockFace upDownFacing, int maxWidth, int maxHeight) {
        BlockFace leftRightFacing = HangingFace.values()[(HangingFace.getByBlockFace(frontFacing).ordinal() + 1) % 4].getBlockFace();
        int offsetTop = maxHeight;
        int offsetLeft = maxWidth;
        Location current = center.clone();
        for (int y = 0; y < offsetTop; y++) {
            current = current.getBlock().getRelative(upDownFacing).getLocation();
            for (int left = 0; left < offsetLeft; left++) {
                current = current.getBlock().getRelative(leftRightFacing).getLocation();
                if (!canHoldPainting(current, frontFacing)) {
                    if (upDownFacing == BlockFace.UP) {
                        offsetLeft = Math.min(offsetLeft, left);
                        offsetTop = Math.min(offsetTop, y);
                    }
                    if (upDownFacing == BlockFace.DOWN) {
                        offsetLeft = Math.max(offsetLeft, left);
                        offsetTop = Math.max(offsetTop, y);
                    }
                }
            }
            current.setX(center.getX());
            current.setZ(center.getZ());
        }
        return new Key(offsetLeft, offsetTop);
    }

    private boolean canHoldPainting(Location where, BlockFace frontFacing) {
        if (!where.getBlock().getType().isSolid()) {
            return false;
        }

        Block inFront = where.clone().getBlock().getRelative(frontFacing);
        if (inFront.getType().isSolid()) {
            return false;
        }
        return true;
    }

    private Collection<Art> getMatchingArt(Key topLeftOffset, Key maxSize) {
        return artBySpawnOffset
            .entries()
            .stream()
            .filter(entry -> {
                Key key = entry.getKey();
                Art art = entry.getValue();
                if (key.getX() > topLeftOffset.getX() || key.getZ() > topLeftOffset.getZ()) {
                    return false;
                }

                if (maxSize.getX() < art.getBlockWidth() || maxSize.getZ() < art.getBlockHeight()) {
                    return false;
                }
                return true;
            })
            .map(Entry::getValue)
            .collect(Collectors.toList());
    }
}
