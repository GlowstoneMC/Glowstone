package net.glowstone.block;

import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.UpdateSignMessage;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.Arrays;

public class GlowSign extends GlowBlockState implements Sign {

    private final String[] lines;

    public GlowSign(GlowBlock block) {
        super(block);
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            throw new IllegalArgumentException("GlowSign: expected WALL_SIGN or SIGN_POST, got " + block.getType());
        }
        lines = new String[4];
    }

    public String[] getLines() {
        return lines.clone();
    }

    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines[index];
    }

    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        if (index < 0 || index >= lines.length) {
            throw new IndexOutOfBoundsException();
        }
        lines[index] = line;
    }

    @Override
    public boolean update(boolean force) {
        boolean result = super.update(force);
        if (result) {
            GlowChunk.Key key = new GlowChunk.Key(getChunk().getX(), getChunk().getZ());
            UpdateSignMessage message = new UpdateSignMessage(getX(), getY(), getZ(), getLines());
            for (GlowPlayer player : getWorld().getRawPlayers()) {
                if (player.canSee(key)) {
                    player.getSession().send(message);
                }
            }
        }
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Internals

    @Override
    public GlowSign shallowClone() {
        GlowSign result = new GlowSign(getBlock());
        System.arraycopy(lines, 0, result.lines, 0, 4);
        return result;
    }

    @Override
    public void destroy() {
        Arrays.fill(lines, null);
    }

    @Override
    public void update(GlowPlayer player) {
        player.getSession().send(new UpdateSignMessage(getX(), getY(), getZ(), getLines()));
    }
}
