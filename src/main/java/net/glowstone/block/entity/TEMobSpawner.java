package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowCreatureSpawner;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import org.bukkit.entity.EntityType;

import java.util.List;

public class TEMobSpawner extends TileEntity {

    private EntityType spawning;
    private int delay;

    public TEMobSpawner(GlowBlock block) {
        super(block);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);

        spawning = EntityType.fromName(tag.get("EntityId", StringTag.class));
        delay = tag.get("Delay", IntTag.class);
    }

    @Override
    public List<Tag> saveNbt() {
        List<Tag> result = super.saveNbt();
        result.add(new StringTag("EntityId", spawning == null ? "" : spawning.getName()));
        result.add(new IntTag("Delay", delay));
        return result;
    }

    @Override
    public GlowBlockState getState() {
        return new GlowCreatureSpawner(block);
    }

    public EntityType getSpawning() {
        return spawning;
    }

    public void setSpawning(EntityType spawning) {
        this.spawning = spawning;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
