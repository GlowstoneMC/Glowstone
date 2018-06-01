package net.glowstone.generator.structures;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.glowstone.generator.objects.TerrainObject;
import net.glowstone.generator.structures.util.StructureBoundingBox;
import net.glowstone.util.BlockStateDelegate;
import org.bukkit.World;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public abstract class GlowStructure implements TerrainObject {

    /** The world to generate the structure in. */
    @Getter
    protected final World world;
    /** The x coordinate of the root chunk. */
    @Getter
    protected final int chunkX;
    /** The z coordinate of the root chunk. */
    @Getter
    protected final int chunkZ;
    private final List<GlowStructurePiece> children = new ArrayList<>();
    @Getter
    @Setter
    private StructureBoundingBox boundingBox;
    @Getter
    @Setter
    private boolean dirty;

    public void addPiece(GlowStructurePiece piece) {
        children.add(piece);
    }

    /**
     * Returns an immutable list of this structure's pieces.
     *
     * @return an immutable list of this structure's pieces
     */
    public List<GlowStructurePiece> getPieces() {
        return ImmutableList.copyOf(children);
    }

    public abstract boolean shouldGenerate(Random random);

    /**
     * Updates the structure's bounding box to be the bounding box of the union of its pieces.
     */
    public void wrapAllPieces() {
        boundingBox = new StructureBoundingBox(
                new Vector(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
                new Vector(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE));
        children.stream().filter(Objects::nonNull)
                .forEach(piece -> boundingBox.expandTo(piece.getBoundingBox()));
    }

    @Override
    public boolean generate(World world, Random random, int sourceX, int sourceY, int sourceZ) {
        return generate(random, sourceX, sourceZ, new BlockStateDelegate());
    }

    /**
     * Attempts to generate this structure.
     *
     * @param random the PRNG to use
     * @param x the x coordinate for the structure's root block
     * @param z the z coordinate for the structure's root block
     * @param delegate the {@link BlockStateDelegate} that will check and update blocks
     * @return whether the structure was successfully generated
     */
    public boolean generate(Random random, int x, int z, BlockStateDelegate delegate) {
        if (boundingBox == null) {
            return false;
        }

        Iterator<GlowStructurePiece> it = children.iterator();
        while (it.hasNext()) {
            GlowStructurePiece piece = it.next();
            if (piece != null && piece.getBoundingBox().intersectsWith(x, z, x + 15, z + 15)
                    && piece.generate(world, random, new StructureBoundingBox(
                            new Vector(x, 1, z), new Vector(x + 15, 511, z + 15)),
                    delegate)) {
                it.remove();
            } else {
                return false;
            }
        }

        return true;
    }
}
