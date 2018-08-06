package net.glowstone.block.flattening;

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
        return null;
    }

    @Override
    public int serialize() {
        return 0;
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
        return FlatteningUtil.getPossibleBlockFaces(getMaterial());
    }
}
