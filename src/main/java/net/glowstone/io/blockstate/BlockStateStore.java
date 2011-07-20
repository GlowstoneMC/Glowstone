package net.glowstone.io.blockstate;

import net.glowstone.block.GlowBlockState;
import net.glowstone.util.nbt.*;

import java.util.HashMap;
import java.util.Map;

public abstract class BlockStateStore<T extends GlowBlockState> {
    private final String id;
    private final Class<T> clazz;

    public BlockStateStore(Class<T> clazz, String id) {
        this.id = id;
        this.clazz = clazz;
    }

    public void load(T state, CompoundTag compound) {
        String checkId = ((StringTag) compound.getValue().get("id")).getValue();
        if (!id.equalsIgnoreCase(checkId)) {
            throw new IllegalArgumentException("Invalid ID loading tile entity, expected " + id + " got " + checkId);
        }
        int checkX = ((IntTag) compound.getValue().get("x")).getValue(), x = state.getX();
        int checkY = ((IntTag) compound.getValue().get("y")).getValue(), y = state.getY();
        int checkZ = ((IntTag) compound.getValue().get("z")).getValue(), z = state.getZ();
        if (x != checkX || y != checkY || z != checkZ) {
            throw new IllegalArgumentException("Invalid coords loading tile entity, expected (" + x + "," + y + "," + z + ") got (" + checkX + "," + checkY + "," + checkZ + ")");
        }
    }

    public Map<String, Tag> save(T state) {
        Map<String, Tag> result = new HashMap<String, Tag>();
        result.put("id", new StringTag("id", id));
        result.put("x", new IntTag("x", state.getX()));
        result.put("y", new IntTag("y", state.getY()));
        result.put("z", new IntTag("z", state.getZ()));
        return result;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return clazz;
    }
}
