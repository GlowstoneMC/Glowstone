package net.glowstone.generator.structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.World;
import org.bukkit.util.Vector;

public abstract class GlowStructure {

    protected final World world;
    protected final int chunkX;
    protected final int chunkZ;
    private final List<GlowStructurePiece> children = new ArrayList<>();
    private StructureBoundingBox boundingBox;
    private boolean dirty;

    public GlowStructure(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public World getWorld() {
        return world;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public StructureBoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(StructureBoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void addPiece(GlowStructurePiece piece) {
        children.add(piece);
    }

    public List<GlowStructurePiece> getPieces() {
        return children;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public abstract boolean shouldGenerate(Random random);

    public void wrapAllPieces() {
        boundingBox = new StructureBoundingBox(
            new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
            new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
        children.stream().filter(Objects::nonNull)
            .forEach(piece -> boundingBox.expandTo(piece.getBoundingBox()));
    }

    public boolean generate(Random random, int x, int z, BlockStateDelegate delegate) {
        if (boundingBox == null) {
            return false;
        }

        Iterator<GlowStructurePiece> it = children.iterator();
        while (it.hasNext()) {
            GlowStructurePiece piece = it.next();
            if (piece != null && piece.getBoundingBox().intersectsWith(x, z, x + 15, z + 15) &&
                piece.generate(world, random,
                    new StructureBoundingBox(new Vector(x, 1, z), new Vector(x + 15, 511, z + 15)),
                    delegate)) {
                it.remove();
            } else {
                return false;
            }
        }

        return true;
    }
}
