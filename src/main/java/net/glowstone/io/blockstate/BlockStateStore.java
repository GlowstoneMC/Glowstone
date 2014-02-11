package net.glowstone.io.blockstate;

import net.glowstone.block.GlowBlockState;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;

import java.util.LinkedList;
import java.util.List;

public abstract class BlockStateStore<T extends GlowBlockState> {
    private final String id;
    private final Class<T> clazz;

    public BlockStateStore(Class<T> clazz, String id) {
        this.id = id;
        this.clazz = clazz;
    }

    public void load(T state, CompoundTag compound) {
        String checkId = compound.get("id", StringTag.class);
        if (!id.equalsIgnoreCase(checkId)) {
            throw new IllegalArgumentException("Invalid ID loading tile entity, expected " + id + " got " + checkId);
        }
        int checkX = compound.get("x", IntTag.class), x = state.getX();
        int checkY = compound.get("y", IntTag.class), y = state.getY();
        int checkZ = compound.get("z", IntTag.class), z = state.getZ();
        if (x != checkX || y != checkY || z != checkZ) {
            throw new IllegalArgumentException("Invalid coords loading tile entity, expected (" + x + "," + y + "," + z + ") got (" + checkX + "," + checkY + "," + checkZ + ")");
        }
    }

    public List<Tag> save(T state) {
        List<Tag> result = new LinkedList<Tag>();
        if (!id.equals("Player")) {
            // players should not have this field
            result.add(new StringTag("id", id));
        }
        result.add(new IntTag("x", state.getX()));
        result.add(new IntTag("y", state.getY()));
        result.add(new IntTag("z", state.getZ()));
        return result;
    }

    public String getId() {
        return id;
    }

    public Class<T> getType() {
        return clazz;
    }
}
