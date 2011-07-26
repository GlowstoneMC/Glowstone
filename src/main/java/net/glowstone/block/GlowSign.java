package net.glowstone.block;

import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.Map;

public class GlowSign extends GlowBlockState implements Sign {
    private String[] lines;

    public GlowSign(GlowBlock block) {
        super(block);
        if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) {
            throw new IllegalArgumentException("GlowSign: expected WALL_SIGN or SIGN_POST, got " + block.getType());
        }
        lines = new String[4];
    }

    @Override
    public String[] getLines() {
        return lines.clone();
    }

    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return lines.clone()[index];
    }

    @Override
    public void setLine(int index, String line) throws IndexOutOfBoundsException {
        if (index >= 4)
            throw new IndexOutOfBoundsException();
        lines[index] = line;
    }

        // Internal mechanisms

    @Override
    public void destroy() {
        for (String line : lines) {
            line = "";
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound, "Sign");
        setLine(0, ((StringTag)compound.getValue().get("Text1")).getValue());
        setLine(1, ((StringTag)compound.getValue().get("Text2")).getValue());
        setLine(2, ((StringTag)compound.getValue().get("Text3")).getValue());
        setLine(3, ((StringTag)compound.getValue().get("Text4")).getValue());
    }

    @Override
    public CompoundTag save() {
        Map<String, Tag> map = super.save("Sign");
        map.put("Line1", new StringTag("Text1", getLine(0)));
        map.put("Line2", new StringTag("Text2", getLine(1)));
        map.put("Line3", new StringTag("Text3", getLine(2)));
        map.put("Line4", new StringTag("Text4", getLine(3)));
        map.put("Logic", new ByteTag("Logic", (byte)0)); // Not sure what this does
        return new CompoundTag("", map);
    }
}
