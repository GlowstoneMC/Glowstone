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
import org.bukkit.block.BlockFace;

public class PaintingSpawner {
    private static final Multimap<Key, Art> artBySpawnOffset = HashMultimap.create();

    private static int MAX_OFFSET_TOP;
    private static int MAX_OFFSET_LEFT;
    private static int MAX_WIDTH;
    private static int MAX_HEIGHT;

    static {
        for (Art art : Art.values()) {
            int offsetLeft = Math.max(0, art.getBlockWidth() / 2 - 1);
            int offsetTop = art.getBlockHeight() / 2;
            artBySpawnOffset.put(new Key(offsetLeft, offsetTop), art);

            if (MAX_OFFSET_TOP < offsetTop) {
                MAX_OFFSET_TOP = offsetTop;
            }
            if (MAX_OFFSET_LEFT < offsetLeft) {
                MAX_OFFSET_LEFT = offsetLeft;
            }

            if (MAX_WIDTH < art.getBlockWidth()) {
                MAX_WIDTH = art.getBlockWidth();
            }

            if (MAX_HEIGHT < art.getBlockHeight()) {
                MAX_HEIGHT = art.getBlockHeight();
            }
        }
    }

    public boolean spawnAt(Location where, BlockFace facing) {
        Art art = getArt(where, HangingFace.values()[(HangingFace.getByBlockFace(facing).ordinal() + 1) % 4]);

        new GlowPainting(where.getBlock().getRelative(facing).getLocation(), facing).setArt(art);


        return true;
    }

    private Art getArt(Location where, HangingFace hangingFace) {
        BlockFace widthFacing = hangingFace.getBlockFace();
        Key topLeftOffset = getOffset(where.clone(), widthFacing, BlockFace.UP, MAX_OFFSET_LEFT, MAX_OFFSET_TOP);

        Location topLeftCorner = where.clone();
        topLeftCorner.add(widthFacing.getModX() * topLeftOffset.getX(), topLeftOffset.getZ(), widthFacing.getModZ() * topLeftOffset.getX());
        Key maxSize = getOffset(topLeftCorner, widthFacing.getOppositeFace(), BlockFace.DOWN, MAX_WIDTH, MAX_HEIGHT);

        Collection<Art> matchingArt = getMatchingArt(topLeftOffset, maxSize);
        return matchingArt.iterator().next();
    }

    private Key getOffset(Location where, BlockFace leftRightFacing, BlockFace upDownFacing, int maxX, int maxY) {
        int offsetTop = maxY;
        int offsetLeft = maxX;
        Location current = where.clone();
        for (int y = 0; y < offsetTop; y++) {
            current = current.getBlock().getRelative(upDownFacing).getLocation();
            for (int left = 0; left < offsetLeft; left++) {
                current = current.getBlock().getRelative(leftRightFacing).getLocation();
                if (!current.getBlock().getType().isSolid()) {
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
            current.setX(where.getX());
            current.setZ(where.getZ());
        }
        return new Key(offsetLeft, offsetTop);
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
