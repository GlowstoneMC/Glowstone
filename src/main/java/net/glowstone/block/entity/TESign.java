package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowSign;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.TextMessage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;

import java.util.Arrays;

public class TESign extends TileEntity {

    private final String[] lines = new String[4];

    public TESign(GlowBlock block) {
        super(block);
        setSaveId("Sign");

        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            throw new IllegalArgumentException("Sign must be WALL_SIGN or SIGN_POST, got " + block.getType());
        }

        Arrays.fill(lines, new TextMessage(""));
    }

    @Override
    public void update(GlowPlayer player) {
        player.sendSignChange(getBlock().getLocation(), lines);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        for (int i = 0; i < lines.length; ++i) {
            String key = "Text" + (i + 1);
            if (tag.isString(key)) {
                lines[i] = tag.getString(key);
            }
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        for (int i = 0; i < lines.length; ++i) {
            tag.putString("Text" + (i + 1), lines[i]);
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowSign(block);
    }

    /**
     * Set the lines of text on the sign.
     * @param text The lines of text.
     * @throws IllegalArgumentException If the wrong number of lines is provided.
     */
    public void setLines(String[] text) {
        if (text.length != lines.length) {
            throw new IllegalArgumentException("Provided lines were length " + text.length + ", must be " + lines.length);
        }

        for (int i = 0; i < lines.length; ++i) {
            lines[i] = text[i];
        }
    }

    /**
     * Get the lines of text on the sign.
     * @return The sign's lines.
     */
    public String[] getLines() {
        String[] result = new String[lines.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = lines[i];
        }
        return result;
    }

}
