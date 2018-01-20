package net.glowstone.block.entity.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.SignEntity;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class GlowSign extends GlowBlockState implements Sign {

    private final String[] lines;

    /**
     * Creates the instance for the given sign block.
     *
     * @param block a sign block (wall or post)
     */
    public GlowSign(GlowBlock block) {
        super(block);
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            throw new IllegalArgumentException(
                "GlowSign: expected WALL_SIGN or SIGN_POST, got " + block.getType());
        }
        lines = getBlockEntity().getLines();
    }

    private SignEntity getBlockEntity() {
        return (SignEntity) getBlock().getBlockEntity();
    }

    @Override
    public String[] getLines() {
        return lines.clone();
    }

    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines[index];
    }

    @Override
    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        lines[index] = line;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            SignEntity sign = getBlockEntity();
            sign.setLines(lines);
            sign.updateInRange();
        }
        return result;
    }

}
