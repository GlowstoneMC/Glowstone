package net.glowstone.generator.structures;

import java.util.Random;

import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public abstract class GlowStructurePiece {

    private BlockFace orientation;
    protected StructureBoundingBox boundingBox;
    private int unknownGD;

    public GlowStructurePiece() { }

    public GlowStructurePiece(Location location, Vector size) {
        orientation = BlockFace.NORTH;
        createNewBoundingBox(location, size);
    }

    public GlowStructurePiece(Random random, Location location, Vector size) {
        orientation = getOrientationFromOrdinal(random.nextInt(4));
        switch (orientation) {
            case EAST:
            case WEST:
                size = new Vector(size.getBlockZ(), size.getBlockY(), size.getBlockX());
                break;
            default:
                break;
        }
        createNewBoundingBox(location, size);
    }

    public void setBoundingBox(StructureBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public StructureBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setGD(int gd) {
        this.unknownGD = gd;
    }

    public int getGD() {
        return unknownGD;
    }

    public void setNumericOrientation(int orientation) {
        this.orientation = getOrientationFromOrdinal(orientation);
    }

    public int getNumericOrientation() {
        switch (orientation) {
            case EAST:
                return 1;
            case SOUTH:
                return 2;
            case WEST:
                return 3;
            default:
                return 0;
        }
    }

    public BlockFace getOrientation() {
        return orientation;
    }

    protected final BlockFace getRelativeFacing(BlockFace face) {
        final BlockFace f = getOrientationFromOrdinal((orientation.ordinal() + face.ordinal()) & 0x3);
        if ((orientation == BlockFace.SOUTH || orientation == BlockFace.WEST) &&
                (face == BlockFace.EAST || face == BlockFace.WEST)) {
            return f.getOppositeFace();
        }
        return f;
    }

    public boolean generate(World world, Random random, StructureBoundingBox boundingBox, BlockStateDelegate delegate) {
        if (boundingBox == null) {
            return false;
        }
        return true;
    }

    private void createNewBoundingBox(Location location, Vector size) {
        final Vector min = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        final Vector max = new Vector(location.getBlockX() + size.getBlockX() - 1,
                location.getBlockY() + size.getBlockY() - 1,
                location.getBlockZ() + size.getBlockZ() - 1);
        boundingBox = new StructureBoundingBox(min, max);
    }

    private static BlockFace getOrientationFromOrdinal(int n) {
        switch (n) {
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.SOUTH;
            case 3:
                return BlockFace.WEST;
            default:
                return BlockFace.NORTH;
        }
    }
}
