package net.glowstone.entity;

import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Hanging;

public abstract class GlowHangingEntity extends GlowEntity implements Hanging {

    protected HangingFace facing = HangingFace.SOUTH;

    public GlowHangingEntity(Location location, BlockFace clickedface) {
        super(location);
        facing = HangingFace.getByBlockFace(clickedface);
    }

    @Override
    public BlockFace getAttachedFace() {
        return facing.getBlockFace().getOppositeFace();
    }

    @Override
    public BlockFace getFacing() {
        return facing.getBlockFace();
    }

    protected int getYaw() {
        switch (getFacing()) {
            case WEST:
                return 64;
            case NORTH:
                return -128;
            case EAST:
                return -64;
            default:
                return 0;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum HangingFace {
        SOUTH(BlockFace.SOUTH),
        WEST(BlockFace.WEST),
        NORTH(BlockFace.NORTH),
        EAST(BlockFace.EAST);

        private static final Map<BlockFace, HangingFace> byBlockFace = new EnumMap<>(BlockFace.class);

        static {
            for (HangingFace hangingFace : values()) {
                byBlockFace.put(hangingFace.blockFace, hangingFace);
            }
        }

        private BlockFace blockFace;

        public static HangingFace getByBlockFace(BlockFace by) {
            return byBlockFace.getOrDefault(by, SOUTH);
        }
    }
}
