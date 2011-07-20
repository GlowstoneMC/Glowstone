package net.glowstone.io.blockstate;

import net.glowstone.block.GlowSign;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;

import java.util.Map;

public class SignStore extends BlockStateStore<GlowSign> {

    public SignStore() {
        super(GlowSign.class, "Sign");
    }

    @Override
    public void load(GlowSign state, CompoundTag compound) {
        super.load(state, compound);
        state.setLine(0, ((StringTag) compound.getValue().get("Text1")).getValue());
        state.setLine(1, ((StringTag) compound.getValue().get("Text2")).getValue());
        state.setLine(2, ((StringTag) compound.getValue().get("Text3")).getValue());
        state.setLine(3, ((StringTag) compound.getValue().get("Text4")).getValue());
    }

    @Override
    public Map<String, Tag> save(GlowSign state) {
        Map<String, Tag> result = super.save(state);
        result.put("Line1", new StringTag("Text1", state.getLine(0)));
        result.put("Line2", new StringTag("Text2", state.getLine(1)));
        result.put("Line3", new StringTag("Text3", state.getLine(2)));
        result.put("Line4", new StringTag("Text4", state.getLine(3)));
        result.put("Logic", new ByteTag("Logic", (byte) 0)); // Not sure what this does
        return result;
    }
}
