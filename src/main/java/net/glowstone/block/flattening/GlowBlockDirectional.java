package net.glowstone.block.flattening;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.glowstone.block.flattening.generated.FlatteningUtil;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

public class GlowBlockDirectional extends GlowBlockData implements Directional {
    private BlockFace facing;

    protected GlowBlockDirectional(Material material) {
        super(material);
    }

    @Override
    public BlockData clone() {
        GlowBlockDirectional clone = new GlowBlockDirectional(getMaterial());
        clone.setFacing(getFacing());
        return clone;
    }

    @Override
    public int serialize() {
        List<BlockFace> possibleBlockFaces = FlatteningUtil.getPossibleBlockFaces(getMaterial());
        return possibleBlockFaces.indexOf(getFacing());
    }

    @Override
    public BlockFace getFacing() {
        return facing;
    }

    @Override
    public void setFacing(BlockFace face) {
        this.facing = face;
    }

    @Override
    public Set<BlockFace> getFaces() {
        return Sets.newHashSet(FlatteningUtil.getPossibleBlockFaces(getMaterial()));
    }
}
