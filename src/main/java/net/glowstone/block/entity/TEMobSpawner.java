package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowCreatureSpawner;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;

public class TEMobSpawner extends TileEntity {

    private EntityType spawning;
    private int delay;

    public TEMobSpawner(GlowBlock block) {
        super(block);
        setSaveId("MobSpawner");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);

        spawning = EntityType.fromName(tag.getString("EntityId"));
        delay = tag.getInt("Delay");
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putString("EntityId", spawning == null ? "" : spawning.getName());
        tag.putInt("Delay", delay);
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
