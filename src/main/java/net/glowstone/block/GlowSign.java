package net.glowstone.block;

import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.Material;
import org.bukkit.block.Sign;

import java.util.Map;
import net.glowstone.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.msg.UpdateSignMessage;

public class GlowSign extends GlowBlockState implements Sign {
    
    private String[] lines;

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
            UpdateSignMessage message = new UpdateSignMessage(getX(), getY(), getZ(), getLines());
            for (GlowPlayer player : getWorld().getRawPlayers()) {
                if (player.canSee(new GlowChunk.Key(getChunk().getX(), getChunk().getZ()))) {
                    player.getSession().send(message);
                }
            }
        }
        return result;
    }

    // Internal mechanisms
    
    @Override
    public GlowSign shallowClone() {
        GlowSign result = new GlowSign(getBlock());
        result.lines = lines;
        return result;
    }

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
